package com.intrbiz.virt.dash.action;

import org.apache.log4j.Logger;

import com.intrbiz.metadata.Action;
import com.intrbiz.virt.model.Network;

public class NetworkActions extends ClusteredAction
{
    private static final Logger logger = Logger.getLogger(NetworkActions.class);
    
    @Action("network.create")
    public void createNetwork(Network network)
    {
    } 
    
    @Action("network.reboot")
    public void rebootNetwork(Network network, boolean force)
    {
    }
    
    @Action("network.start")
    public void startNetwork(Network network)
    {
    }
    
    @Action("network.stop")
    public void stopNetwork(Network network)
    {
    }
    
    @Action("network.terminate")
    public void terminateNetwork(Network network)
    {
    }
}
