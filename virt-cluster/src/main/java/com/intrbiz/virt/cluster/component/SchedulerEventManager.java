package com.intrbiz.virt.cluster.component;

import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.event.schedule.VirtScheduleEvent;

public class SchedulerEventManager implements ClusterComponent
{
    private static final String SCHEDULER_EVENT_QUEUES = "virt.scheduler.event";
    
    private static final String schedulerEventQueueName(String zoneId)
    {
        return SCHEDULER_EVENT_QUEUES + "." + zoneId;
    }
    
    private HazelcastInstance instance;

    @Override
    public void config(ClusterManager manager, Config config)
    {
        QueueConfig queueConfig = config.getQueueConfig(schedulerEventQueueName("*"));
        queueConfig.setAsyncBackupCount(0);
        queueConfig.setBackupCount(0);
        queueConfig.setMaxSize(100);
        queueConfig.setStatisticsEnabled(true);
    }

    @Override
    public void start(ClusterManager manager, HazelcastInstance instance)
    {
        this.instance = instance;
    }

    @Override
    public void shutdown()
    {
        this.instance = null;
    }
    
    public IQueue<VirtScheduleEvent> getZoneSchedulerEventQueue(String zoneId)
    {
        return this.instance.getQueue(schedulerEventQueueName(zoneId));
    }
    
    public boolean sendZoneSchedulerEvent(String zoneId, VirtScheduleEvent event)
    {
        IQueue<VirtScheduleEvent> queue = this.getZoneSchedulerEventQueue(zoneId);
        try
        {
            return queue.offer(event, 5L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
        }
        return false;
    }
}
