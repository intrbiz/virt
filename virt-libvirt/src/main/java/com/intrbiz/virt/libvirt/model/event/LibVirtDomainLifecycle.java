package com.intrbiz.virt.libvirt.model.event;

import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;

public abstract class LibVirtDomainLifecycle extends LibVirtEvent
{
    public LibVirtDomainLifecycle(LibVirtDomain domain)
    {
        super(domain);
    }
}
