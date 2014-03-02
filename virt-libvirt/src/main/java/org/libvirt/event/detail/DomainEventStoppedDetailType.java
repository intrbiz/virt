package org.libvirt.event.detail;

/**
 * Detail of a domain stopped event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventStoppedDetailType">virDomainEventStoppedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventStoppedDetailType {
    
    VIR_DOMAIN_EVENT_STOPPED_SHUTDOWN(0),       /*Normal shutdown*/
    VIR_DOMAIN_EVENT_STOPPED_DESTROYED(1),      /*Forced poweroff from host*/
    VIR_DOMAIN_EVENT_STOPPED_CRASHED(2),        /*Guest crashed*/
    VIR_DOMAIN_EVENT_STOPPED_MIGRATED(3),       /*Migrated off to another host*/
    VIR_DOMAIN_EVENT_STOPPED_SAVED(4),          /*Saved to a state file*/
    VIR_DOMAIN_EVENT_STOPPED_FAILED(5),         /*Host emulator/mgmt failed*/
    VIR_DOMAIN_EVENT_STOPPED_FROM_SNAPSHOT(6);  /*offline snapshot loaded*/

    public final int id;

    private DomainEventStoppedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventStoppedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_STOPPED_SHUTDOWN;
	case 1:
	    return VIR_DOMAIN_EVENT_STOPPED_DESTROYED;
	case 2:
	    return VIR_DOMAIN_EVENT_STOPPED_CRASHED;
	case 3:
	    return VIR_DOMAIN_EVENT_STOPPED_MIGRATED;
	case 4:
	    return VIR_DOMAIN_EVENT_STOPPED_SAVED;
	case 5:
	    return VIR_DOMAIN_EVENT_STOPPED_FAILED;
	case 6:
	    return VIR_DOMAIN_EVENT_STOPPED_FROM_SNAPSHOT;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventStoppedDetailType");
    }
}
