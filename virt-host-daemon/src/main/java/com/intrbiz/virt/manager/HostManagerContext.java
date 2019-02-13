package com.intrbiz.virt.manager;

import java.util.List;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.manager.net.NetManager;
import com.intrbiz.virt.manager.store.StoreManager;
import com.intrbiz.virt.manager.virt.VirtManager;

public interface HostManagerContext
{
    String getHostZone();
    
    String getHostName();
    
    List<HostState> getActiveHostsInZone();
    
    StoreManager getStoreManager();
    
    NetManager getNetManager();
    
    VirtManager getVirtManager();
}
