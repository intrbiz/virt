package com.intrbiz.virt.manager.net.model;

/**
 * A guest NIC which used simple Linux kernel bridges
 */
public class BridgedInterfaceInfo extends InterfaceInfo
{
    private final String bridge;

    public BridgedInterfaceInfo(String mac, String bridge)
    {
        super(mac);
        this.bridge = bridge;
    }

    public String getBridge()
    {
        return this.bridge;
    }

    @Override
    public String toString()
    {
        return "BridgedNICInfo [bridge=" + bridge + ", mac=" + getMac() + "]";
    }
}
