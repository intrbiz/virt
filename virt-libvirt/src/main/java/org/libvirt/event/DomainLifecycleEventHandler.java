package org.libvirt.event;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainEventHandler;
import org.libvirt.LibvirtException;
import org.libvirt.event.detail.DomainEventCrashedDetailType;
import org.libvirt.event.detail.DomainEventDefinedDetailType;
import org.libvirt.event.detail.DomainEventPMSuspendedDetailType;
import org.libvirt.event.detail.DomainEventResumedDetailType;
import org.libvirt.event.detail.DomainEventShutdownDetailType;
import org.libvirt.event.detail.DomainEventStartedDetailType;
import org.libvirt.event.detail.DomainEventStoppedDetailType;
import org.libvirt.event.detail.DomainEventSuspendedDetailType;
import org.libvirt.event.detail.DomainEventUndefinedDetailType;
import org.libvirt.jna.ConnectionPointer;
import org.libvirt.jna.DomainPointer;
import org.libvirt.jna.Libvirt;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Listen to domain lifecycle events
 * 
 * <code><pre>
 * con.domainEventRegisterAny(new DomainLifecycleEventHandler() {
 *     @Override
 *     public void onStarted(Connect connection, Domain domain, DomainEventType event, DomainEventStartedDetailType detail) throws LibvirtException {
 *         System.out.println("Got start event: " + event + "::" + detail + " for domain " + domain.getName());
 *     }
 * });
 * </pre></code>
 * 
 * @author Chris Ellis
 */
public abstract class DomainLifecycleEventHandler extends DomainEventHandler {
    
    protected final Callback _adapt(final Connect connection, final Domain domain) {
	return new Libvirt.virConnectDomainEventCallback() {
            public void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, int event, int detail, Pointer opaque) {
                try {
                    onEvent(connection, newDomain(virDomainPointer), DomainEventType.valueOf(event), detail);
                }
                catch (LibvirtException e) {
                    processDomainEventUncaughtErrorHandler(e);
                }
            }
        };
    }
    
    public final DomainEventID getEventId() {
	return DomainEventID.VIR_DOMAIN_EVENT_ID_LIFECYCLE;
    }
    
    /**
     * Handle all lifecycle events
     * @param connection the connection this handler is registered on
     * @param domain the domain
     * @param event
     * @param eventDetail
     */
    public void onEvent(Connect connection, Domain domain, DomainEventType event, int detail) throws LibvirtException {
	// demux the event into specific handlers, decoding the event detail
	if (DomainEventType.VIR_DOMAIN_EVENT_DEFINED == event) {
	    this.onDefined(connection, domain, event, DomainEventDefinedDetailType.valueOf(detail));
	}
	else if (DomainEventType.VIR_DOMAIN_EVENT_UNDEFINED == event) {
	    this.onUndefined(connection, domain, event, DomainEventUndefinedDetailType.valueOf(detail));
	}
	else if (DomainEventType.VIR_DOMAIN_EVENT_STARTED == event) {
	    this.onStarted(connection, domain, event, DomainEventStartedDetailType.valueOf(detail));
	}
	else if (DomainEventType.VIR_DOMAIN_EVENT_SUSPENDED == event) {
	    this.onSuspended(connection, domain, event, DomainEventSuspendedDetailType.valueOf(detail));
	}
        else if (DomainEventType.VIR_DOMAIN_EVENT_RESUMED == event) {
            this.onResumed(connection, domain, event, DomainEventResumedDetailType.valueOf(detail));    
        }
        else if (DomainEventType.VIR_DOMAIN_EVENT_STOPPED == event) {
            this.onStopped(connection, domain, event, DomainEventStoppedDetailType.valueOf(detail));
        }
        else if (DomainEventType.VIR_DOMAIN_EVENT_SHUTDOWN == event) {
            this.onShutdown(connection, domain, event, DomainEventShutdownDetailType.valueOf(detail));
        }
        else if (DomainEventType.VIR_DOMAIN_EVENT_PMSUSPENDED == event) {
            this.onPMSuspended(connection, domain, event, DomainEventPMSuspendedDetailType.valueOf(detail));
        }
        else if (DomainEventType.VIR_DOMAIN_EVENT_CRASHED == event) {
            this.onCrashed(connection, domain, event, DomainEventCrashedDetailType.valueOf(detail));
        }
    }
    
    protected void onDefined(Connect connection, Domain domain, DomainEventType event, DomainEventDefinedDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onUndefined(Connect connection, Domain domain, DomainEventType event, DomainEventUndefinedDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onStarted(Connect connection, Domain domain, DomainEventType event, DomainEventStartedDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onSuspended(Connect connection, Domain domain, DomainEventType event, DomainEventSuspendedDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onResumed(Connect connection, Domain domain, DomainEventType event, DomainEventResumedDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onStopped(Connect connection, Domain domain, DomainEventType event, DomainEventStoppedDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onShutdown(Connect connection, Domain domain, DomainEventType event, DomainEventShutdownDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onPMSuspended(Connect connection, Domain domain, DomainEventType event, DomainEventPMSuspendedDetailType detail) 
	    throws LibvirtException {
    }
    
    protected void onCrashed(Connect connection, Domain domain, DomainEventType event, DomainEventCrashedDetailType detail) 
	    throws LibvirtException {
    }
}
