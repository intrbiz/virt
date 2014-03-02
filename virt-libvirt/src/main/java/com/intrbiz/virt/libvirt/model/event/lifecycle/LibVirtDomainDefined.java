package com.intrbiz.virt.libvirt.model.event.lifecycle;

import com.intrbiz.virt.libvirt.model.event.LibVirtDomainLifecycle;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;

public class LibVirtDomainDefined extends LibVirtDomainLifecycle
{
    private final Detail detail;

    public LibVirtDomainDefined(LibVirtDomain domain, Detail detail)
    {
        super(domain);
        this.detail = detail;
    }
    
    public final Detail getDetail()
    {
        return this.detail;
    }
    
    protected String toStringDetail()
    {
        return ", detail=" + this.detail;
    }
    
    public enum Detail {
        Added, 
        Updated
    }
}
