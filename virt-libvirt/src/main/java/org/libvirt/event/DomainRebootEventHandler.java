package org.libvirt.event;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainEventHandler;
import org.libvirt.LibvirtException;
import org.libvirt.jna.ConnectionPointer;
import org.libvirt.jna.DomainPointer;
import org.libvirt.jna.Libvirt;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Handle reboot domain events
 * 
 * @author Chris Ellis
 */
public abstract class DomainRebootEventHandler extends DomainEventHandler {
    
    protected final Callback _adapt(final Connect connection, final Domain domain) {
	return new Libvirt.VirConnectDomainEventGenericCallback() {
            public void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, Pointer opaque) {
                try {
                    onEvent(connection, newDomain(virDomainPointer));
                }
                catch (LibvirtException e) {
                    processDomainEventUncaughtErrorHandler(e);
                }
            }
        };
    }
    
    public final DomainEventID getEventId() {
	return DomainEventID.VIR_DOMAIN_EVENT_ID_REBOOT;
    }
    
    /**
     * Handle a reboot domain event
     * @param connection the connection this handler is registered on
     * @param domain the domain
     */
    public abstract void onEvent(Connect connection, Domain domain) throws LibvirtException;
}
