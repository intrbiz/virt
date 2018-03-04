package com.intrbiz.virt.manager.net;

import java.util.Set;

import com.intrbiz.virt.event.model.NetworkEO;

public interface NetManager
{
    void start();
    
    Set<String> getSupportedNetworkTypes();
    
    boolean isSupported(NetworkEO net);
    
    /**
     * Create the given network and return the associated bridge name
     * @param net the network to create
     * @return the local bridge name
     */
    String setupNetwork(NetworkEO net);
}
