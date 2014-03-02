package org.libvirt.event;

/**
 * Domain Lifecycle Event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventType">virDomainEventType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventType {
    
    VIR_DOMAIN_EVENT_DEFINED(0), 
    VIR_DOMAIN_EVENT_UNDEFINED(1), 
    VIR_DOMAIN_EVENT_STARTED(2), 
    VIR_DOMAIN_EVENT_SUSPENDED(3), 
    VIR_DOMAIN_EVENT_RESUMED(4), 
    VIR_DOMAIN_EVENT_STOPPED(5), 
    VIR_DOMAIN_EVENT_SHUTDOWN(6), 
    VIR_DOMAIN_EVENT_PMSUSPENDED(7), 
    VIR_DOMAIN_EVENT_CRASHED(8);

    public final int id;

    private DomainEventType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_DEFINED;
	case 1:
	    return VIR_DOMAIN_EVENT_UNDEFINED;
	case 2:
	    return VIR_DOMAIN_EVENT_STARTED;
	case 3:
	    return VIR_DOMAIN_EVENT_SUSPENDED;
	case 4:
	    return VIR_DOMAIN_EVENT_RESUMED;
	case 5:
	    return VIR_DOMAIN_EVENT_STOPPED;
	case 6:
	    return VIR_DOMAIN_EVENT_SHUTDOWN;
	case 7:
	    return VIR_DOMAIN_EVENT_PMSUSPENDED;
	case 8:
	    return VIR_DOMAIN_EVENT_CRASHED;
	}
	throw new IllegalArgumentException(id + " is not a valid DomainEventType");
    }
}
