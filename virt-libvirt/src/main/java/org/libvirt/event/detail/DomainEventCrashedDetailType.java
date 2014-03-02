package org.libvirt.event.detail;

/**
 * Detail of a domain crashed event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventCrashedDetailType">virDomainEventCrashedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventCrashedDetailType {
    
    VIR_DOMAIN_EVENT_CRASHED_PANICKED(0);   /*Guest was panicked*/

    public final int id;

    private DomainEventCrashedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventCrashedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_CRASHED_PANICKED;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventCrashedDetailType");
    }
}
