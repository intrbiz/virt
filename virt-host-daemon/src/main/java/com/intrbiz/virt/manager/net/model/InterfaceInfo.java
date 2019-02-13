package com.intrbiz.virt.manager.net.model;

public abstract class InterfaceInfo
{
    private final String mac;

    public InterfaceInfo(String mac)
    {
        super();
        this.mac = mac;
    }

    public String getMac()
    {
        return mac;
    }
}
