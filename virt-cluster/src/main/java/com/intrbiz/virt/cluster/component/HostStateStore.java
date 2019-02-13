package com.intrbiz.virt.cluster.component;

import java.util.List;
import java.util.stream.Collectors;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.intrbiz.configuration.Configuration;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.cluster.model.HostStatus;

public class HostStateStore<C extends Configuration> implements ClusterComponent<C>
{
    private static final String HOST_STATE_MAP = "virt.host.state";
    
    private static final String HOST_METADATA_MAP = "virt.host.metadata";
    
    private String localMemberId;
    
    private Cluster cluster;
    
    private IMap<String, HostState> hostState;
    
    private IMap<String, Object> hostMetadata;

    @Override
    public void config(ClusterManager<C> manager, Config config)
    {
        // State map
        MapConfig hostStateMapCfg = config.getMapConfig(HOST_STATE_MAP);
        hostStateMapCfg.setAsyncBackupCount(2);
        hostStateMapCfg.setBackupCount(1);
        hostStateMapCfg.setEvictionPolicy(EvictionPolicy.NONE);
        // Metadata map
        MapConfig hostMetadataMapCfg = config.getMapConfig(HOST_METADATA_MAP);
        hostMetadataMapCfg.setAsyncBackupCount(2);
        hostMetadataMapCfg.setBackupCount(1);
        hostMetadataMapCfg.setEvictionPolicy(EvictionPolicy.NONE);
    }

    @Override
    public void start(ClusterManager<C> manager, HazelcastInstance instance)
    {
        this.cluster = instance.getCluster();
        this.localMemberId = this.cluster.getLocalMember().getUuid();
        this.hostState = instance.getMap(HOST_STATE_MAP);
        this.hostMetadata = instance.getMap(HOST_METADATA_MAP);
    }

    @Override
    public void shutdown()
    {
        this.removeHostState(this.localMemberId);
    }
    
    public String getLocalMemberId()
    {
        return this.localMemberId;
    }
    
    public void setLocalHostState(HostState state)
    {
        state.setId(this.localMemberId);
        this.hostState.set(this.localMemberId, state);
    }
    
    public HostState getLocalHostState()
    {
        return this.getHostState(this.localMemberId);
    }
    
    public HostState getHostState(String id)
    {
        return this.hostState.get(id);
    }
    
    public void removeHostState(String id)
    {
        this.hostState.remove(id);
    }
    
    public List<HostState> getHosts()
    {
        return this.cluster.getMembers().stream()
                .map((m) -> getHostState(m.getUuid()))
                .filter((s) -> s != null)
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .collect(Collectors.toList());
    }
    
    public List<HostState> getActiveHostsInZone(String zoneId)
    {
        return this.cluster.getMembers().stream()
                .map((m) -> getHostState(m.getUuid()))
                .filter((s) -> s != null && s.getZone().equals(zoneId) && s.getState() == HostStatus.ACTIVE)
                .collect(Collectors.toList());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getHostMetadata(String hostName, String manager, String key)
    {
        return (T) this.hostMetadata.get(hostName + "." + manager + "." + key);
    }
    
    public void setHostMetadata(String hostName, String manager, String key, Object value)
    {
        this.hostMetadata.set(hostName + "." + manager + "." + key, value);
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
