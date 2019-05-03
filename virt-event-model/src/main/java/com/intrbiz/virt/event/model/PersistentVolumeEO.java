package com.intrbiz.virt.event.model;

import java.io.Serializable;

public class PersistentVolumeEO implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String source;
    
    private long size;
    
    private String type;
    
    private boolean shared;
    
    private int objectSize;
    
    public PersistentVolumeEO()
    {
        super();
    }

    public PersistentVolumeEO(String source, long size, String type, boolean shared, int objectSize)
    {
        super();
        this.source = source;
        this.size = size;
        this.type = type;
        this.shared = shared;
        this.objectSize = objectSize;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isShared()
    {
        return shared;
    }

    public void setShared(boolean shared)
    {
        this.shared = shared;
    }

    public int getObjectSize()
    {
        return objectSize;
    }

    public void setObjectSize(int objectSize)
    {
        this.objectSize = objectSize;
    }
}
