package com.intrbiz.virt.cluster;

import com.intrbiz.virt.scheduler.SchedulerManager;

public class DashClusterManager extends BaseClusterManager
{
    public DashClusterManager(String instanceName, String environment)
    {
        super(instanceName, environment);
    }

    public DashClusterManager(String environment)
    {
        super(environment);
    }
    
    @Override
    protected void registerDefaultComponents()
    {
        super.registerDefaultComponents();
        this.registerComponent(new SchedulerManager());
    }
    
    public SchedulerManager getSchedulerManager()
    {
        return this.getComponent(SchedulerManager.class);
    }
}
