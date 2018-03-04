package com.intrbiz.virt.cluster;

import com.intrbiz.virt.cluster.component.HostEventManager;
import com.intrbiz.virt.cluster.component.HostStateStore;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;

public class BaseClusterManager extends ClusterManager
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
        this.registerComponent(new HostStateStore());
        this.registerComponent(new MachineStateStore());
        this.registerComponent(new HostEventManager());
        this.registerComponent(new SchedulerEventManager());
    }
    
    public HostStateStore getHostStateStore()
    {
        return this.getComponent(HostStateStore.class);
    }
    
    public MachineStateStore getMachineStateStore()
    {
        return this.getComponent(MachineStateStore.class);
    }
    
    public HostEventManager getHostEventManager()
    {
        return this.getComponent(HostEventManager.class);
    }
    
    public SchedulerEventManager getSchedulerEventManager()
    {
        return this.getComponent(SchedulerEventManager.class);
    }
}
