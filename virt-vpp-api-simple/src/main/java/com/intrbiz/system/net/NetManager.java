package com.intrbiz.system.net;

import java.util.List;

import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;

public interface NetManager
{   
    List<String> getInterfaces();
    
    MACAddress getInterfaceMAC(String name) throws NetException;
    
    MTU getInterfaceMTU(String name);
    
    void createVeth(String name, String peerName) throws NetException;
    
    void destroyVeth(String name) throws NetException;
    
    void promiscuousMode(String name, boolean on) throws NetException;
    
    void setUp(String name) throws NetException;
    
    void setDown(String name) throws NetException;
    
    void setMTU(String name, MTU mtu) throws NetException;
    
    void setMAC(String name, MACAddress mac) throws NetException;
    
    public static NetManager getNetManager()
    {
        return new LinuxNetManager();
    }
}
