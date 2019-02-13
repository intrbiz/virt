package com.intrbiz.virt.cluster.component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.intrbiz.configuration.Configuration;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.cluster.model.RouterState;

public class RouterStateStore<C extends Configuration> implements ClusterComponent<C>
{
    private static final String ROUTER_STATE_MAP = "virt.router.state";
    
    private String localMemberId;
    
    private IMap<String, RouterState> routerState;

    @Override
    public void config(ClusterManager<C> manager, Config config)
    {
        MapConfig routerStateMapCfg = config.getMapConfig(ROUTER_STATE_MAP);
        routerStateMapCfg.setAsyncBackupCount(2);
        routerStateMapCfg.setBackupCount(1);
        routerStateMapCfg.setEvictionPolicy(EvictionPolicy.NONE);
    }

    @Override
    public void start(ClusterManager<C> manager, HazelcastInstance instance)
    {
        this.localMemberId = instance.getCluster().getLocalMember().getUuid();
        this.routerState = instance.getMap(ROUTER_STATE_MAP);
    }

    @Override
    public void shutdown()
    {
    }
    
    public void setLocalRouterState(RouterState state)
    {
        state.setHost(this.localMemberId);
        this.setRouterState(state);
    }
    
    public RouterState getRouterState(String zoneId, UUID accountId)
    {
        return this.routerState.get(RouterState.getId(zoneId, accountId));
    }
    
    public void setRouterState(RouterState state)
    {
        this.routerState.set(state.getId(), state);
    }
    
    public void removeRouterState(String zoneId, UUID accountId)
    {
        this.routerState.remove(RouterState.getId(zoneId, accountId));
    }
    
    public List<RouterState> getRouters()
    {
        return new ArrayList<RouterState>(this.routerState.values());
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
