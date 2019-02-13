package com.intrbiz.virt.scheduler;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hazelcast.core.ILock;
import com.hazelcast.core.IQueue;
import com.intrbiz.Util;
import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.event.VirtEvent;
import com.intrbiz.virt.event.host.ManageMachine;
import com.intrbiz.virt.event.host.ManageVolume;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.PersistentVolumeEO;
import com.intrbiz.virt.event.schedule.CreateMachine;
import com.intrbiz.virt.event.schedule.CreateVolume;
import com.intrbiz.virt.event.schedule.VirtScheduleEvent;
import com.intrbiz.virt.scheduler.model.ZoneSchedulerState;
import com.intrbiz.virt.scheduler.stratergy.machine.MachineScheduleStratergy;
import com.intrbiz.virt.scheduler.stratergy.machine.RandomMachineScheduleStratergy;
import com.intrbiz.virt.scheduler.stratergy.storage.RandomVolumeScheduleStratergy;
import com.intrbiz.virt.scheduler.stratergy.storage.VolumeScheduleStratergy;

public class ZoneScheduler
{
    private static final Logger logger = Logger.getLogger(ZoneScheduler.class);
    
    private final String zoneId;
    
    private final SchedulerManager manager;
    
    private ILock schedulerLock;
    
    private IQueue<VirtScheduleEvent> schedulerEventQueue;
    
    private Thread thread;
    
    private volatile boolean run = false;
    
    private volatile boolean restart = false;
    
    private MachineScheduleStratergy machineStratergy = new RandomMachineScheduleStratergy();
    
    private VolumeScheduleStratergy volumeStratergy = new RandomVolumeScheduleStratergy();
    
    private final String hostName;
    
    public ZoneScheduler(SchedulerManager manager, String zoneId)
    {
        super();
        this.zoneId = zoneId;
        this.manager = manager;
        this.hostName = Util.coalesceEmpty(System.getProperty("host.name"), System.getenv("host_name"));
    }
    
    public String getZoneId()
    {
        return this.zoneId;
    }
    
    public String getHostName()
    {
        return this.hostName;
    }
    
    public void start()
    {
        logger.info("Launching scheduler for " + zoneId);
        this.schedulerLock = this.manager.getZoneSchedulerLock(this.zoneId);
        this.schedulerEventQueue = this.manager.getZoneSchedulerEventQueue(this.zoneId);
        this.thread = new Thread(this::runScheduler);
        this.run = true;
        this.thread.start();
    }
    
    public void shutdown()
    {
        this.run = false;
        try
        {
            this.thread.join();
        }
        catch (InterruptedException e)
        {
        }
    }
    
    public void restart()
    {
        this.restart = true;
    }
    
    private void runScheduler()
    {
        while (this.run)
        {
            // randomly pause before racing for the scheduler lock, this aims to 
            // distribute schedulers over the controllers
            this.randomPause(500, 14_500);
            // obtain the lock for this scheduler or wait until we can get it
            logger.info("Obtaining scheduler lock for zone " + this.zoneId);
            this.schedulerLock.lock();
            try
            {
                this.restart = false;
                logger.info("Obtained scheduler lock for zone " + this.zoneId + ", processing scheduler events");
                // update state
                this.manager.setZoneSchedulerState(new ZoneSchedulerState(this.zoneId, true, this.manager.getLocalMember(), this.hostName));
                // start the scheduling loop
                while (this.run && (! this.restart))
                {
                    // process scheduler events
                    try
                    {
                        VirtEvent event = this.schedulerEventQueue.poll(1L, TimeUnit.SECONDS);
                        if (event != null)
                        {
                            this.processSchedulerEvent(event);
                        }
                    }
                    catch (InterruptedException e)
                    {
                    }
                    catch (Exception e)
                    {
                        logger.warn("Failed to process scheduler event", e);
                    }
                }
                // update state
                this.manager.setZoneSchedulerState(new ZoneSchedulerState(this.zoneId, false, this.manager.getLocalMember(), this.hostName));
            }
            finally
            {
                logger.info("Unlocking scheduler lock for zone " + this.zoneId);
                this.schedulerLock.unlock();
                logger.info("Unlocked scheduler lock for zone " + this.zoneId);
            }
        }
    }
    
    private void randomPause(int min, int vary)
    {
        int delay = (new SecureRandom()).nextInt(vary) + min;
        logger.info("Pausing zone scheduler " + this.zoneId + " for " + delay + "ms");
        try
        {
            Thread.sleep(delay);
        }
        catch (InterruptedException e)
        {
        }
    }
    
    /**
     * Scheduler event dispatch
     */
    private void processSchedulerEvent(VirtEvent event)
    {
        logger.info("Got scheduler event " + event);
        if (event instanceof CreateMachine)
        {
            this.createMachine((CreateMachine) event);
        }
        else if (event instanceof CreateVolume)
        {
            this.createVolume((CreateVolume) event);
        }
    }
    
    /**
     * Create a machine on a host
     */
    private void createMachine(CreateMachine createMachine)
    {
        try
        {
            MachineEO machine = createMachine.getMachine();
            // TODO: Account limits
            // get the current machine state
            MachineState machineState = this.manager.getMachineStore().getMachineState(machine.getId());
            if (machineState == null)
            {
                machineState = new MachineState(machine.getId());
                this.manager.getMachineStore().setMachineState(machineState);
            }
            // attempt to schedule the machine
            try
            {
                List<HostState> activeHostsInZone = this.manager.getHostStore().getActiveHostsInZone(this.zoneId);
                HostState chosenHost = this.machineStratergy.scheduleMachine(activeHostsInZone, machine);
                if (chosenHost == null) throw new SchedulerException("No suitable hosts to create machine.");
                logger.info("Create machine " + machine.getId() + " onto host " + chosenHost.getId() + " (" + chosenHost.getName() + ") in zone " + this.zoneId);
                // update the machine state
                this.manager.getMachineStore().setMachineState(machineState.scheduled(chosenHost.getId()));
                // fire off an event to the host
                this.manager.getHostEvents().sendEvent(chosenHost.getId(), new ManageMachine(machine, ManageMachine.Action.CREATE));
            }
            catch (Exception e)
            {
                // damn, we can't create this machine currently
                // update the machine state
                this.manager.getMachineStore().setMachineState(machineState.pending());
                // fire off error reporting
            }
        }
        catch (Exception e)
        {
            logger.error("Failed to create machine " + createMachine, e);
        }
    }
    
    /**
     * Create a persistent volume
     */
    private void createVolume(CreateVolume createVolume)
    {
        try
        {
            PersistentVolumeEO volume = createVolume.getVolume();
            // TODO: Account limits
            // attempt to create the volume
            try
            {
                List<HostState> activeHostsInZone = this.manager.getHostStore().getActiveHostsInZone(this.zoneId);
                HostState chosenHost = this.volumeStratergy.scheduleVolume(activeHostsInZone, volume);
                if (chosenHost == null) throw new SchedulerException("No suitable hosts to create volume.");
                logger.info("Create volume " + volume + " onto host " + chosenHost.getId() + " (" + chosenHost.getName() + ") in zone " + this.zoneId);
                // fire off an event to the host
                this.manager.getHostEvents().sendEvent(chosenHost.getId(), new ManageVolume(volume, ManageVolume.Action.CREATE));
            }
            catch (Exception e)
            {
                // damn, we can't create this volume currently
                // fire off error reporting
            }
        }
        catch (Exception e)
        {
            logger.error("Failed to create volume " + createVolume, e);
        }
    }
    
}
