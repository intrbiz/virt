package com.intrbiz.virt.libvirt;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.event.EventListener;

import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.model.event.LibVirtEvent;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;


public abstract class LibVirtEventHandler<T extends LibVirtEvent>
{
    protected LibVirtAdapter adapter;
    
    protected LibVirtDomain domain = null;
    
    protected EventListener handler;
    
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
                    this._deregister(this.adapter.getLibVirtConnection(), this.domain == null ? null : this.domain.getLibVirtDomain(), this.handler);
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
    
    final EventListener register(LibVirtAdapter on, LibVirtDomain domain) throws LibvirtException
    {
        this.adapter = on;
        this.domain = domain;
        this.handler = this._register(on.getLibVirtConnection(), domain == null ? null : domain.getLibVirtDomain());
        return this.handler;
    }
    
    protected abstract EventListener _register(Connect connect, Domain domain) throws LibvirtException;
    
    protected abstract void _deregister(Connect connect, Domain domain, EventListener listener) throws LibvirtException;
    
    /**
     * Handle the event
     */
    public abstract void onEvent(T event);
}
