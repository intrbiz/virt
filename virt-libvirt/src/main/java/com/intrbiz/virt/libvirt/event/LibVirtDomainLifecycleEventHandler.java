package com.intrbiz.virt.libvirt.event;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.event.DomainEvent;
import org.libvirt.event.EventListener;
import org.libvirt.event.LifecycleListener;

import com.intrbiz.virt.libvirt.LibVirtEventHandler;
import com.intrbiz.virt.libvirt.model.event.LibVirtDomainLifecycle;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainCrashed;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainDefined;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainPMSuspended;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainResumed;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainShutdown;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainStarted;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainStopped;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainSuspended;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainUndefined;

public abstract class LibVirtDomainLifecycleEventHandler extends LibVirtEventHandler<LibVirtDomainLifecycle>
{
    @Override
    protected EventListener _register(final Connect connect, Domain domain) throws LibvirtException
    {
        LifecycleListener ev = new LifecycleListener()
        {
            @Override
            public int onLifecycleChange(Domain domain, DomainEvent event)
            {
                try
                {
                    switch (event.getType())
                    {
                        case CRASHED:
                            onEvent(new LibVirtDomainCrashed(theDomain(domain), LibVirtDomainCrashed.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case DEFINED:
                            onEvent(new LibVirtDomainDefined(theDomain(domain), LibVirtDomainDefined.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case PMSUSPENDED:
                            onEvent(new LibVirtDomainPMSuspended(theDomain(domain), LibVirtDomainPMSuspended.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case RESUMED:
                            onEvent(new LibVirtDomainResumed(theDomain(domain), LibVirtDomainResumed.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case SHUTDOWN:
                            onEvent(new LibVirtDomainShutdown(theDomain(domain), LibVirtDomainShutdown.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case STARTED:
                            onEvent(new LibVirtDomainStarted(theDomain(domain), LibVirtDomainStarted.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case STOPPED:
                            onEvent(new LibVirtDomainStopped(theDomain(domain), LibVirtDomainStopped.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case SUSPENDED:
                            onEvent(new LibVirtDomainSuspended(theDomain(domain), LibVirtDomainSuspended.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case UNDEFINED:
                            onEvent(new LibVirtDomainUndefined(theDomain(domain), LibVirtDomainUndefined.Detail.values()[event.getDetail().ordinal()]));
                            break;
                        case UNKNOWN:
                            break;
                        default:
                            break;
                    }
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
                return 0;
            }
        };
        connect.addLifecycleListener(ev);
        return ev;
    }

    @Override
    protected void _deregister(Connect connect, Domain domain, EventListener listener) throws LibvirtException
    {
        connect.removeLifecycleListener((LifecycleListener) listener);
    }
}
