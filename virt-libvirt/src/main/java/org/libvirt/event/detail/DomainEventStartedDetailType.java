package org.libvirt.event.detail;

/**
 * Detail of a domain started event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventStartedDetailType">virDomainEventStartedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventStartedDetailType {
    
    VIR_DOMAIN_EVENT_STARTED_BOOTED(0),         /*Normal startup from boot*/
    VIR_DOMAIN_EVENT_STARTED_MIGRATED(1),       /*Incoming migration from another host*/
    VIR_DOMAIN_EVENT_STARTED_RESTORED(2),       /*Restored from a state file*/
    VIR_DOMAIN_EVENT_STARTED_FROM_SNAPSHOT(3),  /*Restored from snapshot*/
    VIR_DOMAIN_EVENT_STARTED_WAKEUP(4);         /*Started due to wakeup event*/

    public final int id;

    private DomainEventStartedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventStartedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_STARTED_BOOTED;
	case 1:
	    return VIR_DOMAIN_EVENT_STARTED_MIGRATED;
	case 2:
	    return VIR_DOMAIN_EVENT_STARTED_RESTORED;
	case 3:
	    return VIR_DOMAIN_EVENT_STARTED_FROM_SNAPSHOT;
	case 4:
	    return VIR_DOMAIN_EVENT_STARTED_WAKEUP;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventStartedDetailType");
    }
}
