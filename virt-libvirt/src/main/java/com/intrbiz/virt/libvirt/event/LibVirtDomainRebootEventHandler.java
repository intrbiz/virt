package com.intrbiz.virt.libvirt.event;

import org.apache.log4j.Logger;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainEventHandler;
import org.libvirt.LibvirtException;
import org.libvirt.event.DomainRebootEventHandler;

import com.intrbiz.virt.libvirt.LibVirtEventHandler;
import com.intrbiz.virt.libvirt.model.event.LibVirtDomainReboot;

public abstract class LibVirtDomainRebootEventHandler extends LibVirtEventHandler<LibVirtDomainReboot>
{
    private Logger logger = Logger.getLogger(LibVirtDomainRebootEventHandler.class);

    @Override
    protected DomainEventHandler _register(final Connect connect, Domain domain) throws LibvirtException
    {
        return connect.domainRebootEventRegister(new DomainRebootEventHandler()
        {
            @Override
            public void onEvent(Connect connection, Domain domain) throws LibvirtException
            {
                logger.trace("Adapting reboot event");
                LibVirtDomainRebootEventHandler.this.onEvent(new LibVirtDomainReboot(theDomain(domain)));
            }
        });
    }

}
