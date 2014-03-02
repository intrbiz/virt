package org.libvirt;

import org.libvirt.event.DomainEventID;
import org.libvirt.jna.DomainPointer;

import com.sun.jna.Callback;


/**
 * <p>
 * Listen to domain events.
 * </p><p>
 * Each domain event has a specific handler which should be used to listen 
 * for events
 * </p>
 * @author Chris Ellis
 */
public abstract class DomainEventHandler {

    private Connect connection = null;

    private int id = -1;
    
    private Domain domain;

    void registered(Connect connection, int id) {
	this.connection = connection;
    }
    
    /**
     * Create a libvirt callback which maps to this handler
     * @param connection
     * @param domain
     * @return
     */
    Callback adapt(final Connect connection, final Domain domain) {
	this.domain = domain;
	return this._adapt(connection, domain);
    }
    
    protected abstract Callback _adapt(final Connect connection, final Domain domain);
    
    /**
     * Utility method for subclasses to get or create the Domain
     * @param virDomainPointer
     * @return
     */
    protected Domain newDomain(DomainPointer virDomainPointer)
    {
	return (this.domain == null && virDomainPointer != null) ? new Domain(connection, virDomainPointer) : this.domain;
    }
    
    protected void processDomainEventUncaughtErrorHandler(Throwable t)
    {
	if (this.connection != null) this.connection.processDomainEventUncaughtErrorHandler(t);
    }
    
    /**
     * Get the ID of this event
     */
    public abstract DomainEventID getEventId();
    
    /**
     * Get the domain this event handler was registered against, if any
     * @return the domain or null
     */
    public Domain getDomain()
    {
	return this.domain;
    }

    /**
     * Get the callback ID of this event handler
     */
    public int getCallbackId() {
	return this.id;
    }

    /**
     * Was this handler successfully registered
     */
    public boolean isRegistered() {
	return this.id != -1;
    }

    /**
     * Remove this event handler from the connection it was registered on
     */
    public synchronized void deregister() throws LibvirtException {
	if (this.id != -1) {
	    this.connection.domainEventDeregisterAny(this.id);
	    this.connection = null;
	    this.id = -1;
	}
    }
}
