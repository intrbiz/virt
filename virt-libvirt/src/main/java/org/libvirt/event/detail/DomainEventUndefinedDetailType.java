package org.libvirt.event.detail;

/**
 * Detail of a domain undefined event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventUndefinedDetailType">virDomainEventUndefinedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventUndefinedDetailType {
    
    VIR_DOMAIN_EVENT_UNDEFINED_REMOVED(0);   /*Deleted the config file*/

    public final int id;

    private DomainEventUndefinedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventUndefinedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_UNDEFINED_REMOVED;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventUndefinedDetailType");
    }
}
