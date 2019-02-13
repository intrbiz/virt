package com.intrbiz.virt.manager.net.model;

import com.intrbiz.virt.libvirt.model.definition.SourceDef.MacVTapMode;

/**
 * A guest NIC which used simple Linux kernel bridges
 */
public class DirectInterfaceInfo extends InterfaceInfo
{
    private final String device;
    
    private final MacVTapMode mode;

    public DirectInterfaceInfo(String mac, String device, MacVTapMode mode)
    {
        super(mac);
        this.device = device;
        this.mode = mode;
    }

    public String getDevice()
    {
        return device;
    }

    public MacVTapMode getMode()
    {
        return mode;
    }

    @Override
    public String toString()
    {
        return "DirectInterfaceInfo [device=" + device + ", mode=" + mode + ", mac=" + getMac() + "]";
    }
}
