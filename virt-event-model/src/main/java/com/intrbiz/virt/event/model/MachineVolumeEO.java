package com.intrbiz.virt.event.model;

import java.io.Serializable;
import java.util.Map;

public class MachineVolumeEO implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public enum VolumeMode {
        CLONE, CREATE, ATTACH, DETACH
    }
    
    private String name;
    
    private String sourceParent;
    
    private String source;
    
    private long size;
    
    private String type;
    
    private Map<String, String> typeMetadata;
    
    private VolumeMode mode; 
    
    public MachineVolumeEO()
    {
    }

    public static MachineVolumeEO clone(String name, String sourceParent, String source, String type)
    {
        MachineVolumeEO vol = new MachineVolumeEO();
        vol.name = name;
        vol.sourceParent = sourceParent;
        vol.source = source;
        vol.size = 0L;
        vol.type = type;
        vol.typeMetadata = null;
        vol.mode = VolumeMode.CLONE;
        return vol;
    }
    
    public static MachineVolumeEO create(String name, String source, long size, String type, Map<String, String> typeMetadata)
    {
        MachineVolumeEO vol = new MachineVolumeEO();
        vol.name = name;
        vol.sourceParent = null;
        vol.source = source;
        vol.size = size;
        vol.type = type;
        vol.typeMetadata = typeMetadata;
        vol.mode = VolumeMode.CREATE;
        return vol;
    }
    
    public static MachineVolumeEO attach(String name, String source, String type)
    {
        MachineVolumeEO vol = new MachineVolumeEO();
        vol.name = name;
        vol.sourceParent = null;
        vol.source = source;
        vol.size = 0L;
        vol.type = type;
        vol.typeMetadata = null;
        vol.mode = VolumeMode.ATTACH;
        return vol;
    }
    
    public static MachineVolumeEO detach(String name)
    {
        MachineVolumeEO vol = new MachineVolumeEO();
        vol.name = name;
        vol.sourceParent = null;
        vol.source = null;
        vol.size = 0L;
        vol.type = null;
        vol.typeMetadata = null;
        vol.mode = VolumeMode.DETACH;
        return vol;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSourceParent()
    {
        return sourceParent;
    }

    public void setSourceParent(String sourceParent)
    {
        this.sourceParent = sourceParent;
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

    public VolumeMode getMode()
    {
        return mode;
    }

    public void setMode(VolumeMode mode)
    {
        this.mode = mode;
    }

    public Map<String, String> getTypeMetadata()
    {
        return typeMetadata;
    }

    public void setTypeMetadata(Map<String, String> typeMetadata)
    {
        this.typeMetadata = typeMetadata;
    }

    @Override
    public String toString()
    {
        return "MachineVolumeEO[name=" + name + ", sourceParent=" + sourceParent + ", source=" + source + ", size=" + size + ", type=" + type + ", mode=" + mode + "]";
    }
}
