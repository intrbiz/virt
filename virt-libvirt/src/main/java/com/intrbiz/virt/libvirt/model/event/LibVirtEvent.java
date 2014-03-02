package com.intrbiz.virt.libvirt.model.event;

import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;

public abstract class LibVirtEvent
{
    private final LibVirtDomain domain;
    
    public LibVirtEvent(LibVirtDomain domain)
    {
        this.domain = domain;
    }
    
    public final LibVirtDomain getDomain()
    {
        return this.domain;
    }
    
    public String toString()
    {
        return this.getClass().getSimpleName() + "[domain=" + this.domain.getName() + this.toStringDetail() + "]";
    }
    
    protected String toStringDetail()
    {
        return "";
    }
}
