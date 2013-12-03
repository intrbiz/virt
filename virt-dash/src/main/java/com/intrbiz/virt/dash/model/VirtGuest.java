package com.intrbiz.virt.dash.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;

public class VirtGuest implements Comparable<VirtGuest>
{
    public enum GuestState {
        DEFINED, STARTING, RUNNING, STOPPING
    }

    private String name;

    private UUID uuid;

    private GuestState state;

    private int cpuCount;

    private long memory;

    private LibVirtDomainDef definition;

    private int vncPort;
    
    private List<VirtGuestDisk> disks = new LinkedList<VirtGuestDisk>();
    
    private List<VirtGuestInterface> interfaces = new LinkedList<VirtGuestInterface>();

    public VirtGuest()
    {
        super();
    }

    public VirtGuest(String name, UUID uuid)
    {
        super();
        this.name = name;
        this.uuid = uuid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public GuestState getState()
    {
        return state;
    }

    public void setState(GuestState state)
    {
        this.state = state;
    }

    public int getCpuCount()
    {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount)
    {
        this.cpuCount = cpuCount;
    }

    public long getMemory()
    {
        return memory;
    }

    public void setMemory(long memory)
    {
        this.memory = memory;
    }

    public LibVirtDomainDef getDefinition()
    {
        return definition;
    }

    public void setDefinition(LibVirtDomainDef definition)
    {
        this.definition = definition;
    }

    public int getVncPort()
    {
        return vncPort;
    }

    public void setVncPort(int vncPort)
    {
        this.vncPort = vncPort;
    }
    
    public List<VirtGuestDisk> getDisks()
    {
        return disks;
    }
    
    public void addDisk(VirtGuestDisk disk)
    {
        this.disks.add(disk);
        Collections.sort(this.disks);
    }

    public List<VirtGuestInterface> getInterfaces()
    {
        return interfaces;
    }
    
    public void addInterface(VirtGuestInterface iface)
    {
        this.interfaces.add(iface);
    }

    @Override
    public int compareTo(VirtGuest o)
    {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VirtGuest other = (VirtGuest) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }
}
