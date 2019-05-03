package com.intrbiz.virt.manager.store.model;

public class BlockVolumeInfo extends VolumeInfo
{    
    private final String devicePath;
    
    public BlockVolumeInfo(long size, String devicePath)
    {
        super(size);
        this.devicePath = devicePath;
    }

    public String getDevicePath()
    {
        return devicePath;
    }

    @Override
    public String toString()
    {
        return "BlockVolumeInfo [devicePath=" + devicePath + ", size=" + getSize() + "]";
    }
    
    
}
