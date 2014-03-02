package com.intrbiz.virt.libvirt;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainEventHandler;
import org.libvirt.LibvirtException;

import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.model.event.LibVirtEvent;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;


public abstract class LibVirtEventHandler<T extends LibVirtEvent>
{
    protected LibVirtAdapter adapter;
    
    protected LibVirtDomain domain = null;
    
    protected DomainEventHandler handler;
    
    public LibVirtEventHandler()
    {
        super();
    }
    
    public final LibVirtAdapter getAdapter()
    {
        return this.adapter;
    }
    
    public final LibVirtDomain getDomain()
    {
        return this.domain;
    }
    
    protected LibVirtDomain theDomain(Domain dom)
    {
        return (this.domain == null && dom != null && this.adapter != null) ? this.adapter.newLibVirtDomain(dom) : this.domain;
    }
    
    public final void deregister()
    {   
        synchronized (this)
        {
            if (this.handler != null)
            {
                try
                {
                    this.handler.deregister();
                }
                catch (LibvirtException e)
                {
                    throw new DataException("Failed to deregister handler", e);
                }
                finally
                {
                    this.handler = null;
                    this.domain = null;
                }
            }
        }
    }
    
    final DomainEventHandler register(LibVirtAdapter on, LibVirtDomain domain) throws LibvirtException
    {
        this.adapter = on;
        this.domain = domain;
        this.handler = this._register(on.getLibVirtConnection(), domain == null ? null : domain.getLibVirtDomain());
        return this.handler;
    }
    
    protected abstract DomainEventHandler _register(Connect connect, Domain domain) throws LibvirtException;
    
    /**
     * Handle the event
     */
    public abstract void onEvent(T event);
}
