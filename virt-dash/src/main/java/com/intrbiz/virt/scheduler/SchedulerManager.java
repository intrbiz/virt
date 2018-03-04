package com.intrbiz.virt.scheduler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IQueue;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.cluster.component.HostEventManager;
import com.intrbiz.virt.cluster.component.HostStateStore;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;
import com.intrbiz.virt.event.schedule.VirtScheduleEvent;

public class SchedulerManager implements ClusterComponent
{
    private static Logger logger = Logger.getLogger(SchedulerManager.class);
    
    private static final String SCHEDULER_LOCKS = "virt.zone.scheduler";
    
    private static final String schedulerLockName(String zoneId)
    {
        return SCHEDULER_LOCKS + "." + zoneId;
    }
    
    private HazelcastInstance instance;
    
    private SchedulerEventManager schedulerEvents;
    
    private List<ZoneScheduler> zoneSchedulers = new CopyOnWriteArrayList<ZoneScheduler>();
    
    private HostStateStore hostStore;
    
    private MachineStateStore machineStore;
    
    private HostEventManager hostEvents;

    public SchedulerManager()
    {
        super();
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
        logger.info("Scheduler Manager starting up");
        this.instance = instance;
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

    public ILock getZoneSchedulerLock(String zoneId)
    {
        return this.instance.getLock(schedulerLockName(zoneId));
    }
    
    public IQueue<VirtScheduleEvent> getZoneSchedulerEventQueue(String zoneId)
    {
        return this.schedulerEvents.getZoneSchedulerEventQueue(zoneId);
    }
    
    public HostStateStore getHostStore()
    {
        return this.hostStore;
    }
    
    public MachineStateStore getMachineStore()
    {
        return this.machineStore;
    }
    
    public HostEventManager getHostEvents()
    {
        return this.hostEvents;
    }
    
    private void startZoneSchedulers()
    {
        // TODO: Get zone list from somewhere
        for (String zoneId : Arrays.asList("uk1.a"))
        {
            ZoneScheduler scheduler = new ZoneScheduler(this, zoneId);
            this.zoneSchedulers.add(scheduler);
            scheduler.start();
        }
    }
}
