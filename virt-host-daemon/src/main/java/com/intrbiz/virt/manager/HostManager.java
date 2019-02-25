package com.intrbiz.virt.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
import com.intrbiz.virt.config.VirtHostCfg;
import com.intrbiz.virt.event.VirtEvent;
import com.intrbiz.virt.event.host.ManageMachine;
import com.intrbiz.virt.manager.net.DefaultNetManager;
import com.intrbiz.virt.manager.net.NetDNetManager;
import com.intrbiz.virt.manager.net.NetManager;
import com.intrbiz.virt.manager.store.CephStoreManager;
import com.intrbiz.virt.manager.store.LocalStoreManager;
import com.intrbiz.virt.manager.store.StoreManager;
import com.intrbiz.virt.manager.virt.LibvirtManager;
import com.intrbiz.virt.manager.virt.VirtManager;
import com.intrbiz.virt.manager.virt.model.HostInfo;

public class HostManager implements ClusterComponent<VirtHostCfg>
{
    private static Logger logger = Logger.getLogger(HostManager.class);
    
    private HostStateStore<VirtHostCfg> hostStore;
    
    private MachineStateStore<VirtHostCfg> machineStore;
    
    private HostEventManager<VirtHostCfg> hostEvents;

    private HostStatus status = HostStatus.JOINING;

    private VirtManager virtManager;

    private StoreManager storeManager;

    private NetManager netManager;
    
    private Timer timer = new Timer();
    
    private TimerTask updateTask;
    
    private String hostZone;
    
    private String hostName;
    
    private VirtHostCfg config;

    public HostManager()
    {
        super();
    }
    
    @Override
    public VirtHostCfg getConfiguration()
    {
        return this.config;
    }
    
    @Override
    public void configure(VirtHostCfg config) throws Exception
    {
        this.config = config;
        // configure basic parameters
        this.hostZone = config.getZone().getName();
        this.hostName = config.getName();
        // configure managers
        // Load the virtulization manager
        this.virtManager = this.createVirtManager(config.getVirtManager().getType());
        this.virtManager.configure(config.getVirtManager());
        // Load the storeage manager
        this.storeManager = this.createStoreManager(config.getStoreManager().getType());
        this.storeManager.configure(config.getStoreManager());
        // Load the network manager
        this.netManager = this.createNetManager(config.getNetManager().getType());
        this.netManager.configure(config.getNetManager());
    }
    
    protected VirtManager createVirtManager(String type)
    {
        switch (Util.coalesce(type, "default"))
        {
            case "libvirt":
            default: return new LibvirtManager();
        }
    }
    
    protected StoreManager createStoreManager(String type)
    {
        switch (Util.coalesce(type, "default"))
        {
            case "ceph": return new CephStoreManager();
            default: return new LocalStoreManager();
        }
    }
    
    protected NetManager createNetManager(String type)
    {
        switch (Util.coalesce(type, "default"))
        {
            case "netd": return new NetDNetManager();
            default: return new DefaultNetManager();
        }
    }

    @Override
    public int order()
    {
        return ORDER_LATE;
    }

    @Override
    public void config(ClusterManager<VirtHostCfg> manager, Config config)
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(ClusterManager<VirtHostCfg> manager, HazelcastInstance instance)
    {
        logger.info("Host Manager starting up");
        logger.info("Host " + this.hostName + " in zone " + this.hostZone);
        this.hostStore = manager.getComponent(HostStateStore.class);
        this.machineStore = manager.getComponent(MachineStateStore.class);
        this.hostEvents = manager.getComponent(HostEventManager.class);
        // start our underlying managers
        HostManagerContext hostManagerContext = this.createHostManagerContext();
        this.netManager.start(hostManagerContext, this.createHostMetadataStoreContext("net"));
        this.storeManager.start(hostManagerContext, this.createHostMetadataStoreContext("store"));
        this.virtManager.start(hostManagerContext, this.createHostMetadataStoreContext("virt"));
        // setup event handler
        this.hostEvents.addLocalEventHandler(this::processVirtEvent);
        // set our initial state
        this.registerLocalHostState();
        // do an initial scan of our VMs
        this.discoverMachines();
        // register remote VM hosts
        this.registerRemoteVMHosts();
        // setup scheduled tasks
        this.updateTask = new TimerTask() {
            public void run()
            {
                update();
            }
        };
        this.timer.scheduleAtFixedRate(this.updateTask, 30_000L, 30_000L);
        // update our status
        this.status = HostStatus.ACTIVE;
        this.registerLocalHostState();
        // start up complete
        logger.info("Host Manager started");
    }

    @Override
    public void shutdown()
    {
        // update our state
        this.status = HostStatus.LEAVING;
        this.registerLocalHostState();
        // shutdown our period task
        this.updateTask.cancel();
    }

    private void update()
    {
        // update our state
        this.registerLocalHostState();
        // register remote VM hosts
        this.registerRemoteVMHosts();
        // scan machines
        this.discoverMachines();
    }
    
    private void registerRemoteVMHosts()
    {
        String ourAddress = this.netManager.getInterconnectAddress();
        Set<String> remoteHosts = new HashSet<String>();
        for (HostState host : this.hostStore.getActiveHostsInZone(this.getHostZone()))
        {
            if (host.getState() == HostStatus.ACTIVE && (! ourAddress.equals(host.getInterconnectAddress())))
            {
                logger.debug("Registering remote VM host " + host);
                remoteHosts.add(host.getInterconnectAddress());
            }
            else
            {
                logger.debug("Ignoring remote VM host " + host);
            }
        }
        // Do a batch register
        this.netManager.registerRemoteVMHosts(remoteHosts);
    }

    private void registerLocalHostState()
    {
        this.hostStore.setLocalHostState(this.getHostState());
    }
    
    private void discoverMachines()
    {
        for (MachineState state : this.virtManager.discoverMachines())
        {
            this.machineStore.mergeLocalMachineState(state);
        }
    }

    private HostState getHostState()
    {
        HostInfo info = this.virtManager.getHostInfo();
        HostState state = new HostState(this.hostZone, this.hostName, this.status, this.config.getCapabilities());
        state.setInterconnectAddress(this.netManager.getInterconnectAddress());
        state.setHostCPUs(info.getHostCPUs());
        state.setHostMemory(info.getHostMemory());
        state.setSupportedMachineTypeFamilies(this.virtManager.getAvailableMachineTypeFamilies());
        state.setSupportedNetworkTypes(this.netManager.getSupportedNetworkTypes());
        state.setSupportedVolumeTypes(this.storeManager.getSupportedVolumeTypes());
        state.setRunningMachines(info.getRunningMachines());
        state.setDefinedMachines(info.getDefinedMachines());
        state.setDefinedMemory(info.getDefinedMemory());
        state.setHugepages2MiBTotal(info.getHugepages2MiBTotal());
        state.setHugepages2MiBFree(info.getHugepages2MiBFree());
        state.setHugepages1GiBTotal(info.getHugepages1GiBTotal());
        state.setHugepages1GiBFree(info.getHugepages1GiBFree());
        return state;
    }
    
    /**
     * Get the zone this host is in
     */
    public String getHostZone()
    {
        return this.hostZone;
    }

    /**
     * Get the name for this host
     */
    public String getHostName()
    {
        return this.hostName;
    }
    
    /**
     * Process a host manager event
     */
    private void processVirtEvent(VirtEvent event)
    {
        logger.info("Got event: " + event);
        if (event instanceof ManageMachine)
        {
            this.updateMachine((ManageMachine) event);
        }
    }
    
    /**
     * Update the state of a machine on this host
     */
    private void updateMachine(ManageMachine update)
    {
        switch (update.getAction())
        {
            case CREATE:
                this.virtManager.createMachine(update.getMachine());
                this.virtManager.start(update.getMachine());
                this.machineStore.mergeLocalMachineState(this.virtManager.getMachine(update.getMachine().getId()));
                break;
            case REBOOT:
                this.virtManager.reboot(update.getMachine(), update.isForce());
                this.machineStore.mergeLocalMachineState(this.virtManager.getMachine(update.getMachine().getId()));
                break;
            case START:
                this.virtManager.start(update.getMachine());
                this.machineStore.mergeLocalMachineState(this.virtManager.getMachine(update.getMachine().getId()));
                break;
            case STOP:
                this.virtManager.stop(update.getMachine(), update.isForce());
                this.machineStore.mergeLocalMachineState(this.virtManager.getMachine(update.getMachine().getId()));
                break;
            case RELEASE:
                this.virtManager.releaseMachine(update.getMachine());
                this.machineStore.removeMachineState(update.getMachine().getId());
                break;
            case TERMINATE:
                this.virtManager.terminateMachine(update.getMachine());
                this.machineStore.removeMachineState(update.getMachine().getId());
                break;
        }
    }
    
    private HostManagerContext createHostManagerContext()
    {
        return new HostManagerContext()
        {
            @Override
            public String getHostZone()
            {
                return hostZone;
            }

            @Override
            public String getHostName()
            {
                return hostName;
            }

            @Override
            public List<HostState> getActiveHostsInZone()
            {
                return hostStore.getActiveHostsInZone(hostZone);
            }
            
            @Override
            public StoreManager getStoreManager()
            {
                return storeManager;
            }
            
            @Override
            public NetManager getNetManager()
            {
                return netManager;
            }
            
            @Override
            public VirtManager getVirtManager()
            {
                return virtManager;
            }
        };
    }
    
    private HostMetadataStoreContext createHostMetadataStoreContext(final String manager)
    {
        return new HostMetadataStoreContext()
        {
            @Override
            public <T> T get(String key)
            {
                return hostStore.getHostMetadata(hostName, manager, key);
            }

            @Override
            public void set(String key, Object value)
            {
                hostStore.setHostMetadata(hostName, manager, key, value);
            }
        };
    }
}
