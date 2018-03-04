package com.intrbiz.virt.manager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.intrbiz.Util;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.cluster.component.HostEventManager;
import com.intrbiz.virt.cluster.component.HostStateStore;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.cluster.model.HostStatus;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.event.VirtEvent;
import com.intrbiz.virt.event.host.CreateMachine;
import com.intrbiz.virt.event.host.RebootMachine;
import com.intrbiz.virt.event.host.StartMachine;
import com.intrbiz.virt.event.host.StopMachine;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.manager.net.DefaultNetManager;
import com.intrbiz.virt.manager.net.NetManager;
import com.intrbiz.virt.manager.store.DefaultStoreManager;
import com.intrbiz.virt.manager.store.StoreManager;
import com.intrbiz.virt.manager.vm.LibvirtManager;
import com.intrbiz.virt.manager.vm.VirtManager;

public class HostManager implements ClusterComponent
{
    private static Logger logger = Logger.getLogger(HostManager.class);
    
    private HostStateStore hostStore;
    
    private MachineStateStore machineStore;
    
    private HostEventManager hostEvents;

    private HostStatus status = HostStatus.JOINING;

    private VirtManager virtManager;

    private StoreManager storeManager;

    private NetManager netManager;
    
    private Timer timer = new Timer();
    
    private TimerTask updateTask;

    public HostManager()
    {
        super();
        this.virtManager = new LibvirtManager();
        this.storeManager = new DefaultStoreManager();
        this.netManager = new DefaultNetManager();
    }
    
    @Override
    public int order()
    {
        return ORDER_LATE;
    }

    @Override
    public void config(ClusterManager manager, Config config)
    {
    }

    @Override
    public void start(ClusterManager manager, HazelcastInstance instance)
    {
        logger.info("Host Manager starting up");
        this.hostStore = manager.getComponent(HostStateStore.class);
        this.machineStore = manager.getComponent(MachineStateStore.class);
        this.hostEvents = manager.getComponent(HostEventManager.class);
        // start our underlying managers
        this.netManager.start();
        this.storeManager.start();
        this.virtManager.start();
        // setup event handler
        this.hostEvents.addLocalEventHandler(this::processVirtEvent);
        // set our initial state
        this.setLocalHostState();
        // do an initial scan of our VMs
        this.setMachineStates();
        // setup scheuled tasks
        this.updateTask = new TimerTask() {
            public void run()
            {
                update();
            }
        };
        this.timer.scheduleAtFixedRate(this.updateTask, 30_000L, 30_000L);
        // update our status
        this.status = HostStatus.ACTIVE;
        // start up complete
        logger.info("Host Manager started");
    }

    @Override
    public void shutdown()
    {
        // update our state
        this.status = HostStatus.LEAVING;
        this.setLocalHostState();
        // shutdown our period task
        this.updateTask.cancel();
    }

    private void update()
    {
        // update our state
        this.setLocalHostState();
        // scan machines
        this.setMachineStates();
    }
    
    private void setMachineStates()
    {
        for (MachineState state : this.virtManager.getMachineStates())
        {
            this.machineStore.setLocalMachineState(state);
        }
    }

    private void setLocalHostState()
    {
        this.hostStore.setLocalHostState(this.getHostState());
    }

    private HostState getHostState()
    {
        HostState state = new HostState(this.getHostZone(), this.getHostName(), this.status);
        state.setHostCPUs(this.virtManager.getHostCPUs());
        state.setHostMemory(this.virtManager.getHostMemory());
        state.setSupportedMachineTypeFamilies(this.virtManager.getAvailableMachineTypeFamilies());
        state.setSupportedNetworkTypes(this.netManager.getSupportedNetworkTypes());
        state.setSupportedVolumeTypes(this.storeManager.getSupportedVolumeTypes());
        state.setRunningMachines(this.virtManager.getRunningMachines());
        state.setDefinedMemory(this.virtManager.getDefinedMemory());
        return state;
    }
    
    private String getHostZone()
    {
        return Util.coalesceEmpty(System.getProperty("host.zone"), System.getenv("host_zone"));
    }

    private String getHostName()
    {
        return Util.coalesceEmpty(System.getProperty("host.name"), System.getenv("host_name"));
    }
    
    private void processVirtEvent(VirtEvent event)
    {
        logger.info("Got event: " + event);
        if (event instanceof CreateMachine)
        {
            CreateMachine runMachine = (CreateMachine) event;
            this.runMachine(runMachine.getMachine());
        }
        else if (event instanceof RebootMachine)
        {
            RebootMachine reboot = (RebootMachine) event;
            this.rebootMachine(reboot.getMachineId());
        }
        else if (event instanceof StartMachine)
        {
            StartMachine start = (StartMachine) event;
            this.startMachine(start.getMachineId());
        }
        else if (event instanceof StopMachine)
        {
            StopMachine stop = (StopMachine) event;
            this.stopMachine(stop.getMachineId());
        }
    }
    
    private void runMachine(MachineEO machine)
    {
        try
        {
            logger.info("Running machine: " + machine.getId() + " " + machine.getName());
            this.virtManager.createMachine(machine, this.storeManager, this.netManager);
        }
        catch (Exception e)
        {
            logger.error("Failed to run machine", e);
        }
    }
    
    private void rebootMachine(UUID id)
    {
        try
        {
            logger.info("Rebooting machine: " + id);
            this.virtManager.rebootMachine(id);
        }
        catch (Exception e)
        {
            logger.error("Failed to reboot machine", e);
        }
    }
    
    private void startMachine(UUID id)
    {
        try
        {
            logger.info("Starting machine: " + id);
            this.virtManager.startMachine(id);
        }
        catch (Exception e)
        {
            logger.error("Failed to start machine", e);
        }
    }
    
    private void stopMachine(UUID id)
    {
        try
        {
            logger.info("Stopping machine: " + id);
            this.virtManager.stopMachine(id);
        }
        catch (Exception e)
        {
            logger.error("Failed to stop machine", e);
        }
    }
}
