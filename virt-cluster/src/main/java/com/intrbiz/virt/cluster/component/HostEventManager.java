package com.intrbiz.virt.cluster.component;

import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.Member;
import com.intrbiz.configuration.Configuration;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.cluster.event.VirtEventConsumer;
import com.intrbiz.virt.cluster.event.VirtEventHandler;
import com.intrbiz.virt.event.host.VirtHostEvent;

public class HostEventManager<C extends Configuration> implements ClusterComponent<C>
{
    private static final String HOST_EVENT_QUEUES = "virt.host.event";
    
    private static final String hostEventQueueName(String memberId)
    {
        return HOST_EVENT_QUEUES + "." + memberId;
    }
    
    private String localMemberId;
    
    private HazelcastInstance instance;
    
    private Cluster cluster;
    
    private IQueue<VirtHostEvent> localEventQueue;
    
    private VirtEventConsumer<VirtHostEvent> localConsumer;

    @Override
    public void config(ClusterManager<C> manager, Config config)
    {
        QueueConfig queueConfig = config.getQueueConfig(hostEventQueueName("*"));
        queueConfig.setAsyncBackupCount(0);
        queueConfig.setBackupCount(0);
        queueConfig.setMaxSize(100);
        queueConfig.setStatisticsEnabled(true);
    }

    @Override
    public void start(ClusterManager<C> manager, HazelcastInstance instance)
    {
        this.instance = instance;
        this.cluster = instance.getCluster();
        this.localMemberId = this.cluster.getLocalMember().getUuid();
        // start our event consumer
        this.localEventQueue = instance.getQueue(hostEventQueueName(this.localMemberId));
        this.localConsumer = new VirtEventConsumer<VirtHostEvent>(this.localEventQueue);
        this.localConsumer.start();
    }

    @Override
    public void shutdown()
    {
        this.localConsumer.shutdown();
        this.localEventQueue.destroy();
        this.instance = null;
    }
    
    public String getLocalMemberId()
    {
        return this.localMemberId;
    }
    
    public IQueue<VirtHostEvent> getHostEventQueue(String memberId)
    {
        if (!this.localMemberId.equals(memberId))
            return this.instance.getQueue(hostEventQueueName(memberId));
        return null;
    }
    
    public boolean sendEvent(Member targetMember, VirtHostEvent event)
    {
        return this.sendEvent(targetMember.getUuid(), event);
    }
    
    public boolean sendEvent(String targetMemberId, VirtHostEvent event)
    {
        IQueue<VirtHostEvent> queue = this.getHostEventQueue(targetMemberId);
        try
        {
            return queue.offer(event, 5L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
        }
        return false;
    }
    
    public void addLocalEventHandler(VirtEventHandler<VirtHostEvent> handler)
    {
        this.localConsumer.addLocalEventHandler(handler);
    }
    

    @Override
    public void configure(C cfg) throws Exception
    {
    }

    @Override
    public C getConfiguration()
    {
        return null;
    }
}
