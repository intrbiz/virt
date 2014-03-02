package com.intrbiz.virt.libvirt.model.event;

import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;

public class LibVirtDomainReboot extends LibVirtEvent
{
    public LibVirtDomainReboot(LibVirtDomain domain)
    {
        super(domain);
    }
}
