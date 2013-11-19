package com.intrbiz.virt.libvirt.model.wrapper;

import com.intrbiz.virt.libvirt.model.definition.DiskDef;

public abstract class LibVirtDisk
{
    private String type;

    private String device;

    private String driverName;

    private String driverType;

    private String sourceUrl;

    private String targetBus;

    private String targetName;

    public LibVirtDisk(DiskDef def)
    {
        super();
        this.type = def.getType();
        this.device = def.getDevice();
        if (def.getDriver() != null) this.driverName = def.getDriver().getName();
        if (def.getDriver() != null) this.driverType = def.getDriver().getType();
        if (def.getSource() != null) this.sourceUrl = def.getSource().getFile();
        if (def.getTarget() != null) this.targetName = def.getTarget().getDev();
        if (def.getTarget() != null) this.targetBus = def.getTarget().getBus();
    }

    public String getType()
    {
        return type;
    }

    public String getDevice()
    {
        return device;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public String getDriverType()
    {
        return driverType;
    }

    public String getSourceUrl()
    {
        return sourceUrl;
    }

    public String getTargetName()
    {
        return targetName;
    }

    public String getTargetBus()
    {
        return targetBus;
    }

    public abstract LibVirtDiskInfo getDiskInfo();
    
    public abstract LibVirtDiskStats getDiskStats();
}
