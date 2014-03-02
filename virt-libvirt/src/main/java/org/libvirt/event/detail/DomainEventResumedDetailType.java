package org.libvirt.event.detail;

/**
 * Detail of a domain resumed event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventResumedDetailType">virDomainEventResumedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventResumedDetailType {
    
    VIR_DOMAIN_EVENT_RESUMED_UNPAUSED(0),       /*Normal resume due to admin unpause*/
    VIR_DOMAIN_EVENT_RESUMED_MIGRATED(1),       /*Resumed for completion of migration*/
    VIR_DOMAIN_EVENT_RESUMED_FROM_SNAPSHOT(2);  /*Resumed from snapshot*/

    public final int id;

    private DomainEventResumedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventResumedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_RESUMED_UNPAUSED;
	case 1:
	    return VIR_DOMAIN_EVENT_RESUMED_MIGRATED;
	case 2:
	    return VIR_DOMAIN_EVENT_RESUMED_FROM_SNAPSHOT;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventResumedDetailType");
    }
}
