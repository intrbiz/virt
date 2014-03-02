package com.intrbiz.virt.libvirt.model.event.lifecycle;

import com.intrbiz.virt.libvirt.model.event.LibVirtDomainLifecycle;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;

public class LibVirtDomainStarted extends LibVirtDomainLifecycle
{
    private final Detail detail;

    public LibVirtDomainStarted(LibVirtDomain domain, Detail detail)
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
        Booted, 
        Migrated, 
        Restored, 
        FromSnapshot, 
        Wakeup
    }
}
