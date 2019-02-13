package com.intrbiz.virt.manager.store.model;

public abstract class VolumeInfo
{
    private final long size;
    
    public VolumeInfo(long size)
    {
        super();
        this.size = size;
    }
    
    public long getSize()
    {
        return this.size;
    }
}
