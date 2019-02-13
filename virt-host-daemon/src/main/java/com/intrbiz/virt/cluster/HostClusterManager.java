package com.intrbiz.virt.cluster;

import com.intrbiz.virt.config.VirtHostCfg;
import com.intrbiz.virt.manager.HostManager;

public class HostClusterManager extends BaseClusterManager<VirtHostCfg>
{   
    public HostClusterManager(String instanceName, String environment)
    {
        super(instanceName, environment);
    }

    public HostClusterManager(String environment)
    {
        super(environment);
    }
    
    @Override
    protected void registerDefaultComponents()
    {
        super.registerDefaultComponents();
        this.registerComponent(new HostManager());
    }
    
    public HostManager getHostManager()
    {
        return this.getComponent(HostManager.class);
    }
}
