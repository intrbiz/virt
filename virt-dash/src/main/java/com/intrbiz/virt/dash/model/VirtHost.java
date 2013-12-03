package com.intrbiz.virt.dash.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.intrbiz.virt.dash.cfg.VirtGuestImage;

public class VirtHost implements Comparable<VirtHost>
{
    private String name;

    private String address;

    private String url;

    private String arch;

    private int cpuCount;

    private int cpuSpeed;

    private long memory;

    private long definedMemory;

    private ConcurrentMap<String, VirtGuest> guests = new ConcurrentHashMap<String, VirtGuest>();

    private boolean up = false;

    private List<VirtGuestImage> images = new LinkedList<VirtGuestImage>();

    private List<String> bridges = new LinkedList<String>();
    
    private List<VirtStoragePool> storagePools = new LinkedList<VirtStoragePool>();

    public VirtHost()
    {
        super();
    }

    public VirtHost(String name, String address, String url)
    {
        super();
        this.name = name;
        this.address = address;
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getArch()
    {
        return arch;
    }

    public void setArch(String arch)
    {
        this.arch = arch;
    }

    public int getCpuCount()
    {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount)
    {
        this.cpuCount = cpuCount;
    }

    public int getCpuSpeed()
    {
        return cpuSpeed;
    }

    public void setCpuSpeed(int cpuSpeed)
    {
        this.cpuSpeed = cpuSpeed;
    }

    public long getMemory()
    {
        return memory;
    }

    public void setMemory(long memory)
    {
        this.memory = memory;
    }

    public long getDefinedMemory()
    {
        return definedMemory;
    }

    public void setDefinedMemory(long definedMemory)
    {
        this.definedMemory = definedMemory;
    }

    public long getAvailableMemory()
    {
        return this.memory - this.definedMemory;
    }

    public boolean isUp()
    {
        return up;
    }

    public void setUp(boolean up)
    {
        this.up = up;
    }

    public List<VirtGuestImage> getImages()
    {
        return images;
    }

    public void setImages(List<VirtGuestImage> images)
    {
        this.images = images;
    }

    public List<VirtGuest> getGuests()
    {
        List<VirtGuest> l = new LinkedList<VirtGuest>();
        l.addAll(guests.values());
        Collections.sort(l);
        return l;
    }

    public VirtGuest addGuest(VirtGuest guest)
    {
        this.guests.put(guest.getName(), guest);
        return guest;
    }

    public void removeGuest(String name)
    {
        this.guests.remove(name);
    }

    public VirtGuest getGuest(String name)
    {
        return this.guests.get(name);
    }

    public VirtGuestImage getImage(String name)
    {
        for (VirtGuestImage img : this.images)
        {
            if (name.equals(img.getName())) return img;
        }
        return null;
    }

    public List<String> getBridges()
    {
        return bridges;
    }

    public void addBridge(String name)
    {
        this.bridges.add(name);
        Collections.sort(this.bridges);
    }    

    public List<VirtStoragePool> getStoragePools()
    {
        return storagePools;
    }
    
    public void addStoragePool(VirtStoragePool storagePool)
    {
        this.storagePools.add(storagePool);
        Collections.sort(this.storagePools);
    }

    @Override
    public int compareTo(VirtHost o)
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
        VirtHost other = (VirtHost) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }
}
