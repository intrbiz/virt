package com.intrbiz.virt.cluster.component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.intrbiz.configuration.Configuration;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.ClusterManager;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.cluster.model.health.MachineHealth;

public class MachineStateStore<C extends Configuration> implements ClusterComponent<C>
{
    private static final String MACHINE_STATE_MAP = "virt.machine.state";
    
    private static final String MACHINE_HEALTH_MAP = "virt.machine.health";
    
    private String localMemberId;
    
    private IMap<UUID, MachineState> machineState;
    
    private IMap<UUID, MachineHealth> machineHealth;

    @Override
    public void config(ClusterManager<C> manager, Config config)
    {
        MapConfig machineStateMapCfg = config.getMapConfig(MACHINE_STATE_MAP);
        machineStateMapCfg.setAsyncBackupCount(2);
        machineStateMapCfg.setBackupCount(1);
        machineStateMapCfg.setEvictionPolicy(EvictionPolicy.NONE);
        MapConfig machineHealthMapCfg = config.getMapConfig(MACHINE_HEALTH_MAP);
        machineHealthMapCfg.setAsyncBackupCount(1);
        machineHealthMapCfg.setEvictionPolicy(EvictionPolicy.LRU);
        machineHealthMapCfg.setTimeToLiveSeconds((int) TimeUnit.MINUTES.toSeconds(5));
    }

    @Override
    public void start(ClusterManager<C> manager, HazelcastInstance instance)
    {
        this.localMemberId = instance.getCluster().getLocalMember().getUuid();
        this.machineState = instance.getMap(MACHINE_STATE_MAP);
        this.machineHealth = instance.getMap(MACHINE_HEALTH_MAP);
    }

    @Override
    public void shutdown()
    {
    }
    
    public void mergeLocalMachineState(MachineState state)
    {
        state.setHost(this.localMemberId);
        this.setMachineState(state);
    }
    
    public MachineState getMachineState(UUID id)
    {
        return this.machineState.get(id);
    }
    
    public void setMachineState(MachineState state)
    {
        this.machineState.put(state.getId(), state);
    }
    
    public void removeMachineState(UUID id)
    {
        this.machineState.remove(id);
        this.machineHealth.remove(id);
    }
    
    public List<MachineState> getMachines()
    {
        return new ArrayList<MachineState>(this.machineState.values());
    }
    
    public MachineHealth getMachineHealth(UUID id)
    {
        return this.machineHealth.get(id);
    }
    
    public void setMachineHealth(UUID id, MachineHealth health)
    {
        this.machineHealth.set(id, health);
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
