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
    
    private String cfgIPv4;
    
    private List<MachineInterfaceEO> interfaces = new LinkedList<MachineInterfaceEO>();
    
    private List<MachineVolumeEO> volumes = new LinkedList<MachineVolumeEO>();
    
    private MachineAdminStatus adminStatus;
    
    private String placementRule;
    
    private AccountEO account;
    
    public MachineEO()
    {
        super();
    }

    public MachineEO(UUID id, String zone, String name, String machineTypeFamily, String machineType, int cpus, long memory, String cfgMac, String cfgAddr, MachineAdminStatus adminStatus, String placementRule, AccountEO account)
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
        this.cfgIPv4 = cfgAddr;
        this.adminStatus = adminStatus;
        this.placementRule = placementRule;
        this.account = account;
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

    public String getCfgIPv4()
    {
        return cfgIPv4;
    }

    public void setCfgIPv4(String cfgIPv4)
    {
        this.cfgIPv4 = cfgIPv4;
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

    public String getPlacementRule()
    {
        return placementRule;
    }

    public void setPlacementRule(String placementRule)
    {
        this.placementRule = placementRule;
    }

    public AccountEO getAccount()
    {
        return account;
    }

    public void setAccount(AccountEO account)
    {
        this.account = account;
    }

    @Override
    public String toString()
    {
        return "MachineEO [id=" + id + ", zone=" + zone + ", name=" + name + ", machineTypeFamily=" + machineTypeFamily + ", machineType=" + machineType + ", cpus=" + cpus + ", memory=" + memory + ", cfgMac=" + cfgMac + ", cfgIPv4=" + cfgIPv4 + ", interfaces=" + interfaces + ", volumes=" + volumes + ", adminStatus=" + adminStatus + ", placementRule=" + placementRule + ", account=" + account + "]";
    }
}
