package com.intrbiz.system.net;

import java.util.List;

public interface NetManager
{   
    List<String> getInterfaces();
    
    String getInterfaceMAC(String name) throws NetException;
    
    int getInterfaceMTU(String name);
    
    void createVeth(String name, String peerName) throws NetException;
    
    void destroyVeth(String name) throws NetException;
    
    void promiscuousMode(String name, boolean on) throws NetException;
    
    void setUp(String name) throws NetException;
    
    void setDown(String name) throws NetException;
    
    void setMTU(String name, int mtu) throws NetException;
    
    void setMAC(String name, String mac) throws NetException;
    
    public static NetManager getNetManager()
    {
        return new LinuxNetManager();
    }
}
