package com.intrbiz.virt.event.model;

import java.io.Serializable;

public class MachineInterfaceEO implements Serializable
{
    private static final long serialVersionUID = 1L;
 
    private String name;
    
    private String mac;
    
    private NetworkEO network;
    
    public MachineInterfaceEO()
    {
        super();
    }

    public MachineInterfaceEO(String name, String mac, NetworkEO network)
    {
        super();
        this.name = name;
        this.mac = mac;
        this.network = network;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMac()
    {
        return mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public NetworkEO getNetwork()
    {
        return network;
    }

    public void setNetwork(NetworkEO network)
    {
        this.network = network;
    }

    @Override
    public String toString()
    {
        return "MachineInterfaceEO[name=" + name + ", mac=" + mac + ", network=" + network + "]";
    }
    
    
}
