package com.intrbiz.virt.libvirt.event;

import org.apache.log4j.Logger;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainEventHandler;
import org.libvirt.LibvirtException;
import org.libvirt.event.DomainEventType;
import org.libvirt.event.DomainLifecycleEventHandler;
import org.libvirt.event.detail.DomainEventCrashedDetailType;
import org.libvirt.event.detail.DomainEventDefinedDetailType;
import org.libvirt.event.detail.DomainEventPMSuspendedDetailType;
import org.libvirt.event.detail.DomainEventResumedDetailType;
import org.libvirt.event.detail.DomainEventShutdownDetailType;
import org.libvirt.event.detail.DomainEventStartedDetailType;
import org.libvirt.event.detail.DomainEventStoppedDetailType;
import org.libvirt.event.detail.DomainEventSuspendedDetailType;
import org.libvirt.event.detail.DomainEventUndefinedDetailType;

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
import com.intrbiz.virt.libvirt.util.Util;

public abstract class LibVirtDomainLifecycleEventHandler extends LibVirtEventHandler<LibVirtDomainLifecycle>
{
    private Logger logger = Logger.getLogger(LibVirtDomainLifecycleEventHandler.class);

    @Override
    protected DomainEventHandler _register(final Connect connect, Domain domain) throws LibvirtException
    {
        return connect.domainLifecycleEventRegister(new DomainLifecycleEventHandler()
        {
            @Override
            public void onEvent(Connect connection, Domain domain, DomainEventType event, int detail) throws LibvirtException
            {
                logger.trace("Adapting event " + event + "::" + detail);
                super.onEvent(connection, domain, event, detail);
            }

            @Override
            protected void onDefined(Connect connection, Domain domain, DomainEventType event, DomainEventDefinedDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainDefined(theDomain(domain), Util.valueOf(LibVirtDomainDefined.Detail.class, detail.id)));
            }

            @Override
            protected void onUndefined(Connect connection, Domain domain, DomainEventType event, DomainEventUndefinedDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainUndefined(theDomain(domain), Util.valueOf(LibVirtDomainUndefined.Detail.class, detail.id)));
            }

            @Override
            protected void onStarted(Connect connection, Domain domain, DomainEventType event, DomainEventStartedDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainStarted(theDomain(domain), Util.valueOf(LibVirtDomainStarted.Detail.class, detail.id)));
            }

            @Override
            protected void onSuspended(Connect connection, Domain domain, DomainEventType event, DomainEventSuspendedDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainSuspended(theDomain(domain), Util.valueOf(LibVirtDomainSuspended.Detail.class, detail.id)));
            }

            @Override
            protected void onResumed(Connect connection, Domain domain, DomainEventType event, DomainEventResumedDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainResumed(theDomain(domain), Util.valueOf(LibVirtDomainResumed.Detail.class, detail.id)));
            }

            @Override
            protected void onStopped(Connect connection, Domain domain, DomainEventType event, DomainEventStoppedDetailType detail) throws LibvirtException
            {                
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainStopped(theDomain(domain), Util.valueOf(LibVirtDomainStopped.Detail.class, detail.id)));
            }

            @Override
            protected void onShutdown(Connect connection, Domain domain, DomainEventType event, DomainEventShutdownDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainShutdown(theDomain(domain), Util.valueOf(LibVirtDomainShutdown.Detail.class, detail.id)));
            }

            @Override
            protected void onPMSuspended(Connect connection, Domain domain, DomainEventType event, DomainEventPMSuspendedDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainPMSuspended(theDomain(domain), Util.valueOf(LibVirtDomainPMSuspended.Detail.class, detail.id)));
            }

            @Override
            protected void onCrashed(Connect connection, Domain domain, DomainEventType event, DomainEventCrashedDetailType detail) throws LibvirtException
            {
                LibVirtDomainLifecycleEventHandler.this.onEvent(new LibVirtDomainCrashed(theDomain(domain), Util.valueOf(LibVirtDomainCrashed.Detail.class, detail.id)));
            }
        });
    }

}
