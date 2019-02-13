package com.intrbiz.virt.cluster.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class HostState implements Serializable, Comparable<HostState>
{
    private static final long serialVersionUID = 1L;

    private String id;

    private int hostCPUs;

    private long hostMemory;

    private Set<String> supportedMachineTypeFamilies = new HashSet<String>();

    private Set<String> supportedVolumeTypes = new HashSet<String>();

    private Set<String> supportedNetworkTypes = new HashSet<String>();
    
    private Set<String> capabilities = new HashSet<String>();

    private HostStatus state;

    private String zone;

    private String name;

    private long lastUpdated;

    private int runningMachines;

    private int definedMachines;

    private int definedCPUs;

    private long definedMemory;
    
    private String interconnectAddress;
    
    private long hugepages2MiBTotal = 0;
    
    private long hugepages2MiBFree = 0;
    
    private long hugepages1GiBTotal = 0;
    
    private long hugepages1GiBFree = 0;

    public HostState()
    {
        super();
    }

    public HostState(String zone, String name, HostStatus state, Set<String> capabilities)
    {
        super();
        this.zone = zone;
        this.name = name;
        this.state = state;
        this.capabilities = capabilities;
        this.lastUpdated = System.currentTimeMillis();
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

    public String getInterconnectAddress()
    {
        return interconnectAddress;
    }

    public void setInterconnectAddress(String interconnectAddress)
    {
        this.interconnectAddress = interconnectAddress;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public HostStatus getState()
    {
        return state;
    }

    public void setState(HostStatus state)
    {
        this.state = state;
    }

    public int getHostCPUs()
    {
        return hostCPUs;
    }

    public void setHostCPUs(int hostCPUs)
    {
        this.hostCPUs = hostCPUs;
    }

    public long getHostMemory()
    {
        return hostMemory;
    }

    public void setHostMemory(long hostMemory)
    {
        this.hostMemory = hostMemory;
    }

    public Set<String> getSupportedMachineTypeFamilies()
    {
        return supportedMachineTypeFamilies;
    }

    public void setSupportedMachineTypeFamilies(Set<String> supportedMachineTypeFamilies)
    {
        this.supportedMachineTypeFamilies = supportedMachineTypeFamilies;
    }

    public Set<String> getSupportedVolumeTypes()
    {
        return supportedVolumeTypes;
    }

    public void setSupportedVolumeTypes(Set<String> supportedVolumeTypes)
    {
        this.supportedVolumeTypes = supportedVolumeTypes;
    }

    public Set<String> getSupportedNetworkTypes()
    {
        return supportedNetworkTypes;
    }

    public void setSupportedNetworkTypes(Set<String> supportedNetworkTypes)
    {
        this.supportedNetworkTypes = supportedNetworkTypes;
    }

    public long getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdatedAgo()
    {
        return System.currentTimeMillis() - this.lastUpdated;
    }

    public int getRunningMachines()
    {
        return runningMachines;
    }

    public void setRunningMachines(int runningMachines)
    {
        this.runningMachines = runningMachines;
    }

    public long getDefinedMemory()
    {
        return definedMemory;
    }

    public void setDefinedMemory(long definedMemory)
    {
        this.definedMemory = definedMemory;
    }

    public int getDefinedMachines()
    {
        return definedMachines;
    }

    public void setDefinedMachines(int definedMachines)
    {
        this.definedMachines = definedMachines;
    }

    public int getDefinedCPUs()
    {
        return definedCPUs;
    }

    public void setDefinedCPUs(int definedCPUs)
    {
        this.definedCPUs = definedCPUs;
    }

    public long getHugepages2MiBTotal()
    {
        return hugepages2MiBTotal;
    }

    public void setHugepages2MiBTotal(long hugepages2MiBTotal)
    {
        this.hugepages2MiBTotal = hugepages2MiBTotal;
    }

    public long getHugepages2MiBFree()
    {
        return hugepages2MiBFree;
    }

    public void setHugepages2MiBFree(long hugepages2MiBFree)
    {
        this.hugepages2MiBFree = hugepages2MiBFree;
    }

    public long getHugepages1GiBTotal()
    {
        return hugepages1GiBTotal;
    }

    public void setHugepages1GiBTotal(long hugepages1GiBTotal)
    {
        this.hugepages1GiBTotal = hugepages1GiBTotal;
    }

    public long getHugepages1GiBFree()
    {
        return hugepages1GiBFree;
    }

    public void setHugepages1GiBFree(long hugepages1GiBFree)
    {
        this.hugepages1GiBFree = hugepages1GiBFree;
    }

    public boolean hasCapability(String capability)
    {
        return this.capabilities.contains(capability);
    }

    public Set<String> getCapabilities()
    {
        return capabilities;
    }

    public void setCapabilities(Set<String> capabilities)
    {
        this.capabilities = capabilities;
    }

    @Override
    public int compareTo(HostState o)
    {
        return this.zone.equals(o.zone) ? this.zone.compareTo(o.zone) : this.name.compareTo(o.name);
    }

    @Override
    public String toString()
    {
        return "HostState [id=" + id + ", hostCPUs=" + hostCPUs + ", hostMemory=" + hostMemory + ", supportedMachineTypeFamilies=" + supportedMachineTypeFamilies + ", supportedVolumeTypes=" + supportedVolumeTypes + ", supportedNetworkTypes=" + supportedNetworkTypes + ", state=" + state + ", zone=" + zone + ", name=" + name + ", lastUpdated=" + lastUpdated + ", runningMachines=" + runningMachines + ", definedMachines=" + definedMachines + ", definedCPUs=" + definedCPUs + ", definedMemory=" + definedMemory + ", interconnectAddress=" + interconnectAddress + "]";
    }
}
