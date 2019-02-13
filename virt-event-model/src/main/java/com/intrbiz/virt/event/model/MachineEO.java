package com.intrbiz.virt.event.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MachineEO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private UUID id;
    
    private String zone;
    
    private String name;
    
    private String machineTypeFamily;
    
    private String machineType;
    
    private int cpus;
    
    private long memory;
    
    private String cfgMac;
    
    private List<MachineInterfaceEO> interfaces = new LinkedList<MachineInterfaceEO>();
    
    private List<MachineVolumeEO> volumes = new LinkedList<MachineVolumeEO>();
    
    private MachineAdminStatus adminStatus;
    
    public MachineEO()
    {
        super();
    }

    public MachineEO(UUID id, String zone, String name, String machineTypeFamily, String machineType, int cpus, long memory, String cfgMac, MachineAdminStatus adminStatus)
    {
        super();
        this.id = id;
        this.zone = zone;
        this.name = name;
        this.machineTypeFamily = machineTypeFamily;
        this.machineType = machineType;
        this.cpus = cpus;
        this.memory = memory;
        this.cfgMac = cfgMac;
        this.adminStatus = adminStatus;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getZone()
    {
        return zone;
    }

    public void setZone(String zone)
    {
        this.zone = zone;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getCpus()
    {
        return cpus;
    }

    public void setCpus(int cpus)
    {
        this.cpus = cpus;
    }

    public String getMachineTypeFamily()
    {
        return machineTypeFamily;
    }

    public void setMachineTypeFamily(String machineType)
    {
        this.machineTypeFamily = machineType;
    }

    public String getMachineType()
    {
        return machineType;
    }

    public void setMachineType(String machineType)
    {
        this.machineType = machineType;
    }

    public long getMemory()
    {
        return memory;
    }

    public void setMemory(long memory)
    {
        this.memory = memory;
    }

    public String getCfgMac()
    {
        return cfgMac;
    }

    public void setCfgMac(String cfgMac)
    {
        this.cfgMac = cfgMac;
    }

    public List<MachineInterfaceEO> getInterfaces()
    {
        return interfaces;
    }

    public void setInterfaces(List<MachineInterfaceEO> nics)
    {
        this.interfaces = nics;
    }

    public List<MachineVolumeEO> getVolumes()
    {
        return volumes;
    }

    public void setVolumes(List<MachineVolumeEO> volumes)
    {
        this.volumes = volumes;
    }

    public MachineAdminStatus getAdminStatus()
    {
        return adminStatus;
    }

    public void setAdminStatus(MachineAdminStatus adminStatus)
    {
        this.adminStatus = adminStatus;
    }

    @Override
    public String toString()
    {
        return "MachineEO[id=" + id + ", zone=" + zone + ", name=" + name + ", machineTypeFamily=" + machineTypeFamily + ", adminStatus=" + this.adminStatus + ", cpus=" + cpus + ", memory=" + memory + ", cfgMac=" + cfgMac + ", interfaces=" + interfaces + ", volumes=" + volumes + "]";
    }
}
