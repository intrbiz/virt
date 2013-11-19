package com.intrbiz.virt.libvirt.model.wrapper;

import com.intrbiz.virt.libvirt.model.definition.InterfaceDef;

public abstract class LibVirtInterface
{
    private String type;

    private String name;

    private String macAddress;

    private String bridge;

    public LibVirtInterface(InterfaceDef def)
    {
        this.type = def.getType();
        if (def.getMac() != null) this.macAddress = def.getMac().getAddress();
        if (def.getSource() != null) this.bridge = def.getSource().getBridge();
        if (def.getTarget() != null) this.name = def.getTarget().getDev();
    }

    public String getType()
    {
        return type;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public String getBridge()
    {
        return bridge;
    }

    public String getName()
    {
        return name;
    }

    public abstract LibVirtInterfaceStats getInterfaceStats();
}
