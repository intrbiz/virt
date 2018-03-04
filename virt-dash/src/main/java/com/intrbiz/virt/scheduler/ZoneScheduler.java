package com.intrbiz.virt.scheduler;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hazelcast.core.ILock;
import com.hazelcast.core.IQueue;
import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.event.VirtEvent;
import com.intrbiz.virt.event.host.CreateMachine;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.schedule.ScheduleMachine;
import com.intrbiz.virt.event.schedule.VirtScheduleEvent;
import com.intrbiz.virt.scheduler.model.ZoneSchedulerState;
import com.intrbiz.virt.scheduler.stratergy.MachineScheduleStratergy;
import com.intrbiz.virt.scheduler.stratergy.RandomMachineScheduleStratergy;

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
    
    public ZoneScheduler(SchedulerManager manager, String zoneId)
    {
        super();
        this.zoneId = zoneId;
        this.manager = manager;
    }
    
    public String getZoneId()
    {
        return this.zoneId;
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
                this.manager.setZoneSchedulerState(new ZoneSchedulerState(this.zoneId, true, this.manager.getLocalMember()));
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
                this.manager.setZoneSchedulerState(new ZoneSchedulerState(this.zoneId, false, this.manager.getLocalMember()));
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
        if (event instanceof ScheduleMachine)
        {
            ScheduleMachine scheduleMachine = (ScheduleMachine) event;
            this.scheduleMachine(scheduleMachine.getMachine());
        }
    }
    
    /**
     * Schedule a machine on a host
     */
    private void scheduleMachine(MachineEO machine)
    {
        try
        {
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
                // get the currently available hosts to schedule on to
                List<HostState> activeHostsInZone = this.manager.getHostStore().getActiveHostsInZone(this.zoneId);
                // choose a host to schedule too
                HostState chosenHost = this.machineStratergy.scheduleMachine(activeHostsInZone, machine);
                if (chosenHost == null) throw new SchedulerException("No suitable hosts to schedule machine onto.");
                // great, we know where to schedule the machine
                logger.info("Schedule machine " + machine.getId() + " onto host " + chosenHost.getId() + " (" + chosenHost.getName() + ") in zone " + this.zoneId);
                // update the machine state
                this.manager.getMachineStore().setMachineState(machineState.scheduled(chosenHost.getId()));
                // fire off an event to the host
                this.manager.getHostEvents().sendEvent(chosenHost.getId(), new CreateMachine(machine));
            }
            catch (Exception e)
            {
                // damn, we can't schedule this machine currently
                // update the machine state
                this.manager.getMachineStore().setMachineState(machineState.pending());
                // fire off error reporting
            }
        }
        catch (Exception e)
        {
            logger.error("Failed to schedule machine " + machine.getId(), e);
        }
    }
    
}
