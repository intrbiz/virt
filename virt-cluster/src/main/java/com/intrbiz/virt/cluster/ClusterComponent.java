package com.intrbiz.virt.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.intrbiz.configuration.Configurable;
import com.intrbiz.configuration.Configuration;

public interface ClusterComponent<C extends Configuration> extends Comparable<ClusterComponent<C>>, Configurable<C>
{   
    public static final int ORDER_NORMAL = 5000;
    
    public static final int ORDER_LATE = 10000;
    
    public static final int ORDER_EARLY = 0;
    
    @Override
    default int compareTo(ClusterComponent<C> o)
    {
        return Integer.compare(this.order(), o.order());
    }

    default int order()
    {
        return ORDER_NORMAL;
    }
    
    void config(ClusterManager<C> manager, Config config);
    
    void start(ClusterManager<C> manager, HazelcastInstance instance);
    
    void shutdown();
}
