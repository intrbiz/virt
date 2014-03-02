package org.libvirt.event.detail;

/**
 * Detail of a domain suspended event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventSuspendedDetailType">virDomainEventSuspendedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventSuspendedDetailType {
    
    VIR_DOMAIN_EVENT_SUSPENDED_PAUSED(0),         /*Normal suspend due to admin pause*/
    VIR_DOMAIN_EVENT_SUSPENDED_MIGRATED(1),       /*Suspended for offline migration*/
    VIR_DOMAIN_EVENT_SUSPENDED_IOERROR(2),        /*Suspended due to a disk I/O error*/
    VIR_DOMAIN_EVENT_SUSPENDED_WATCHDOG(3),       /*Suspended due to a watchdog firing*/
    VIR_DOMAIN_EVENT_SUSPENDED_RESTORED(4),       /*Restored from paused state file*/
    VIR_DOMAIN_EVENT_SUSPENDED_FROM_SNAPSHOT(5),  /*Restored from paused snapshot*/
    VIR_DOMAIN_EVENT_SUSPENDED_API_ERROR(6);      /*suspended after failure during libvirt API call*/

    public final int id;

    private DomainEventSuspendedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventSuspendedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_SUSPENDED_PAUSED;
	case 1:
	    return VIR_DOMAIN_EVENT_SUSPENDED_MIGRATED;
	case 2:
	    return VIR_DOMAIN_EVENT_SUSPENDED_IOERROR;
	case 3:
	    return VIR_DOMAIN_EVENT_SUSPENDED_WATCHDOG;
	case 4:
	    return VIR_DOMAIN_EVENT_SUSPENDED_RESTORED;
	case 5:
	    return VIR_DOMAIN_EVENT_SUSPENDED_FROM_SNAPSHOT;
	case 6:
	    return VIR_DOMAIN_EVENT_SUSPENDED_API_ERROR;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventSuspendedDetailType");
    }
}
