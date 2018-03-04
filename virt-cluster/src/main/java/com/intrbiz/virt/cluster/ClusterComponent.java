package com.intrbiz.virt.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;

public interface ClusterComponent extends Comparable<ClusterComponent>
{   
    public static final int ORDER_NORMAL = 5000;
    
    public static final int ORDER_LATE = 10000;
    
    public static final int ORDER_EARLY = 0;
    
    @Override
    default int compareTo(ClusterComponent o)
    {
        return Integer.compare(this.order(), o.order());
    }

    default int order()
    {
        return ORDER_NORMAL;
    }
    
    void config(ClusterManager manager, Config config);
    
    void start(ClusterManager manager, HazelcastInstance instance);
    
    void shutdown();
}
