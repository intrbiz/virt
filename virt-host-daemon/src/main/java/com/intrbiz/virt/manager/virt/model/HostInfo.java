package com.intrbiz.virt.manager.virt.model;

import com.intrbiz.system.sysfs.SysFs;

public class HostInfo
{
    private int hostCPUs;
    
    private long hostMemory;
    
    private int runningMachines;
    
    private int definedMachines;
    
    private long definedMemory;
    
    private long hugepages2MiBTotal = 0;
    
    private long hugepages2MiBFree = 0;
    
    private long hugepages1GiBTotal = 0;
    
    private long hugepages1GiBFree = 0;

    public HostInfo()
    {
        super();
    }
    
    public HostInfo(int hostCPUs, long hostMemory, int runningMachines, int definedMachines, long definedMemory, SysFs sysFs)
    {
        super();
        this.hostCPUs = hostCPUs;
        this.hostMemory = hostMemory;
        this.runningMachines = runningMachines;
        this.definedMachines = definedMachines;
        this.definedMemory = definedMemory;
        this.hugepages2MiBTotal = sysFs.getHugepages2MiBTotal();
        this.hugepages2MiBFree = sysFs.getHugepages2MiBFree();
        this.hugepages1GiBTotal = sysFs.getHugepages1GiBTotal();
        this.hugepages1GiBFree = sysFs.getHugepages1GiBFree();
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

    public int getRunningMachines()
    {
        return runningMachines;
    }

    public void setRunningMachines(int runningMachines)
    {
        this.runningMachines = runningMachines;
    }

    public int getDefinedMachines()
    {
        return definedMachines;
    }

    public void setDefinedMachines(int definedMachines)
    {
        this.definedMachines = definedMachines;
    }

    public long getDefinedMemory()
    {
        return definedMemory;
    }

    public void setDefinedMemory(long definedMemory)
    {
        this.definedMemory = definedMemory;
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
}
