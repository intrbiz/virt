package com.intrbiz.virt.libvirt.model.wrapper;

public class LibVirtDiskInfo
{
    private final long capacity;

    private final long allocated;

    private final long physical;

    public LibVirtDiskInfo(long cap, long alloc, long phys)
    {
        this.capacity = cap;
        this.allocated = alloc;
        this.physical = phys;
    }

    public long getCapacity()
    {
        return capacity;
    }

    public long getAllocated()
    {
        return allocated;
    }

    public long getPhysical()
    {
        return physical;
    }
}
