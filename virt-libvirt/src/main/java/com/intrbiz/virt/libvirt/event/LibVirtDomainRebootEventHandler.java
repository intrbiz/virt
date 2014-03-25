package com.intrbiz.virt.libvirt.event;

import org.apache.log4j.Logger;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.event.EventListener;
import org.libvirt.event.LifecycleListener;
import org.libvirt.event.RebootListener;

import com.intrbiz.virt.libvirt.LibVirtEventHandler;
import com.intrbiz.virt.libvirt.model.event.LibVirtDomainReboot;

public abstract class LibVirtDomainRebootEventHandler extends LibVirtEventHandler<LibVirtDomainReboot>
{
    private Logger logger = Logger.getLogger(LibVirtDomainRebootEventHandler.class);

    @Override
    protected EventListener _register(final Connect connect, Domain domain) throws LibvirtException
    {
        RebootListener ev = new RebootListener()
        {
            @Override
            public void onReboot(Domain domain)
            {
                logger.trace("Adapting reboot event");
                LibVirtDomainRebootEventHandler.this.onEvent(new LibVirtDomainReboot(theDomain(domain)));
            }
        };
        connect.addRebootListener(ev);
        return ev;
    }
    

    @Override
    protected void _deregister(Connect connect, Domain domain, EventListener listener) throws LibvirtException
    {
        connect.removeLifecycleListener((LifecycleListener) listener);
    }
}
