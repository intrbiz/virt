package com.intrbiz.virt.dash.action;

import static com.intrbiz.balsa.BalsaContext.*;

import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.cluster.DashClusterManager;
import com.intrbiz.virt.cluster.component.HostEventManager;
import com.intrbiz.virt.cluster.component.HostStateStore;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;

public class ClusteredAction
{
    
    protected DashClusterManager getClusterManager()
    {
        return ((VirtDashApp) Balsa().app()).getClusterManager();
    }

    protected SchedulerEventManager<VirtDashCfg> getSchedulerEventManager()
    {
        return this.getClusterManager().getSchedulerEventManager();
    }
    
    @SuppressWarnings("unchecked")
    public HostStateStore<VirtDashCfg> getHostStateStore()
    {
        return this.getClusterManager().getComponent(HostStateStore.class);
    }
    
    @SuppressWarnings("unchecked")
    public MachineStateStore<VirtDashCfg> getMachineStateStore()
    {
        return this.getClusterManager().getComponent(MachineStateStore.class);
    }
    
    @SuppressWarnings("unchecked")
    public HostEventManager<VirtDashCfg> getHostEventManager()
    {
        return this.getClusterManager().getComponent(HostEventManager.class);
    }
}
