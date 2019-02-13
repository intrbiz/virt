package com.intrbiz.virt.scheduler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.Member;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.cluster.component.HostEventManager;
import com.intrbiz.virt.cluster.component.HostStateStore;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.event.schedule.VirtScheduleEvent;
import com.intrbiz.virt.model.Zone;
import com.intrbiz.virt.scheduler.model.ZoneSchedulerState;

public class SchedulerManager implements ClusterComponent<VirtDashCfg>
{
    private static Logger logger = Logger.getLogger(SchedulerManager.class);
    
    private static final String SCHEDULER_LOCKS = "virt.zone.scheduler.lock";
    
    private static final String SCHEDULER_STATE_MAP = "virt.zone.scheduler.state";
    
    private static final String schedulerLockName(String zoneId)
    {
        return SCHEDULER_LOCKS + "." + zoneId;
    }
    
    private HazelcastInstance instance;
    
    private SchedulerEventManager<VirtDashCfg> schedulerEvents;
    
    private List<ZoneScheduler> zoneSchedulers = new CopyOnWriteArrayList<ZoneScheduler>();
    
    private HostStateStore<VirtDashCfg> hostStore;
    
    private MachineStateStore<VirtDashCfg> machineStore;
    
    private HostEventManager<VirtDashCfg> hostEvents;
    
    private IMap<String, ZoneSchedulerState> schedulerState;
    
    private Cluster cluster;

    public SchedulerManager()
    {
        super();
    }
    
    @Override
    public void configure(VirtDashCfg cfg) throws Exception
    {        
    }

    @Override
    public VirtDashCfg getConfiguration()
    {
        return null;
    }

    @Override
    public int order()
    {
        return ORDER_LATE;
    }

    @Override
    public void config(ClusterManager<VirtDashCfg> manager, Config config)
    {
        MapConfig machineStateMapCfg = config.getMapConfig(SCHEDULER_STATE_MAP);
        machineStateMapCfg.setAsyncBackupCount(2);
        machineStateMapCfg.setBackupCount(1);
        machineStateMapCfg.setEvictionPolicy(EvictionPolicy.NONE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(ClusterManager<VirtDashCfg> manager, HazelcastInstance instance)
    {
        logger.info("Scheduler Manager starting up");
        this.instance = instance;
        this.schedulerState = instance.getMap(SCHEDULER_STATE_MAP);
        this.cluster = instance.getCluster();
        this.schedulerEvents = manager.getComponent(SchedulerEventManager.class);
        this.hostStore = manager.getComponent(HostStateStore.class);
        this.hostEvents = manager.getComponent(HostEventManager.class);
        this.machineStore = manager.getComponent(MachineStateStore.class);
        this.startZoneSchedulers();
        logger.info("Scheduler Manager started up");
    }

    @Override
    public void shutdown()
    {
        for (ZoneScheduler scheduler : this.zoneSchedulers)
        {
            scheduler.shutdown();
        }
        this.zoneSchedulers.clear();
    }
    
    public Member getLocalMember()
    {
        return this.cluster.getLocalMember();
    }

    public ILock getZoneSchedulerLock(String zoneId)
    {
        return this.instance.getLock(schedulerLockName(zoneId));
    }
    
    public IQueue<VirtScheduleEvent> getZoneSchedulerEventQueue(String zoneId)
    {
        return this.schedulerEvents.getZoneSchedulerEventQueue(zoneId);
    }
    
    public HostStateStore<VirtDashCfg> getHostStore()
    {
        return this.hostStore;
    }
    
    public MachineStateStore<VirtDashCfg> getMachineStore()
    {
        return this.machineStore;
    }
    
    public HostEventManager<VirtDashCfg> getHostEvents()
    {
        return this.hostEvents;
    }
    
    public ZoneSchedulerState getZoneSchedulerState(String zone)
    {
        return this.schedulerState.get(zone);
    }
    
    public void setZoneSchedulerState(ZoneSchedulerState state)
    {
        this.schedulerState.set(state.getId(), state);
    }
    
    private void startZoneSchedulers()
    {
        try (VirtDB db = VirtDB.connect())
        {
            for (Zone zone : db.listZones())
            {
                ZoneScheduler scheduler = new ZoneScheduler(this, zone.getName());
                this.zoneSchedulers.add(scheduler);
                scheduler.start();
            }
        }
    }
}
