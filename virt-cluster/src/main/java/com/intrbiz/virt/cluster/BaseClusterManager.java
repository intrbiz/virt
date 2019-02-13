package com.intrbiz.virt.cluster;

import com.intrbiz.configuration.Configuration;
import com.intrbiz.virt.cluster.component.HostEventManager;
import com.intrbiz.virt.cluster.component.HostStateStore;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;

public class BaseClusterManager<C extends Configuration> extends ClusterManager<C>
{
    public BaseClusterManager(String instanceName, String environment)
    {
        super(instanceName, environment);
    }

    public BaseClusterManager(String environment)
    {
        super(environment);
    }
    
    @Override
    protected void registerDefaultComponents()
    {
        this.registerComponent(new HostStateStore<C>());
        this.registerComponent(new MachineStateStore<C>());
        this.registerComponent(new HostEventManager<C>());
        this.registerComponent(new SchedulerEventManager<C>());
    }
    
    @SuppressWarnings("unchecked")
    public HostStateStore<C> getHostStateStore()
    {
        return this.getComponent(HostStateStore.class);
    }
    
    @SuppressWarnings("unchecked")
    public MachineStateStore<C> getMachineStateStore()
    {
        return this.getComponent(MachineStateStore.class);
    }
    
    @SuppressWarnings("unchecked")
    public HostEventManager<C> getHostEventManager()
    {
        return this.getComponent(HostEventManager.class);
    }
    
    @SuppressWarnings("unchecked")
    public SchedulerEventManager<C> getSchedulerEventManager()
    {
        return this.getComponent(SchedulerEventManager.class);
    }
}
