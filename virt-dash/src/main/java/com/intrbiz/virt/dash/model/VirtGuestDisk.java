package com.intrbiz.virt.dash.model;

public class VirtGuestDisk implements Comparable<VirtGuestDisk>
{
    private String type;

    private String device;

    private String targetBus;

    private String targetDevice;

    private String driverName;

    private String driverType;

    private String sourceUrl;

    public VirtGuestDisk()
    {
        super();
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getDevice()
    {
        return device;
    }

    public void setDevice(String device)
    {
        this.device = device;
    }

    public String getTargetBus()
    {
        return targetBus;
    }

    public void setTargetBus(String targetBus)
    {
        this.targetBus = targetBus;
    }

    public String getTargetDevice()
    {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice)
    {
        this.targetDevice = targetDevice;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
    }

    public String getDriverType()
    {
        return driverType;
    }

    public void setDriverType(String driverType)
    {
        this.driverType = driverType;
    }

    public String getSourceUrl()
    {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl)
    {
        this.sourceUrl = sourceUrl;
    }

    @Override
    public int compareTo(VirtGuestDisk o)
    {
        return this.targetDevice.compareTo(o.targetDevice);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((targetDevice == null) ? 0 : targetDevice.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VirtGuestDisk other = (VirtGuestDisk) obj;
        if (targetDevice == null)
        {
            if (other.targetDevice != null) return false;
        }
        else if (!targetDevice.equals(other.targetDevice)) return false;
        return true;
    }

}
