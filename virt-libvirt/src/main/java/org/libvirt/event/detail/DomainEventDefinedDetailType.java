package org.libvirt.event.detail;

/**
 * Detail of a domain defined event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventDefinedDetailType">virDomainEventDefinedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventDefinedDetailType {
    
    VIR_DOMAIN_EVENT_DEFINED_ADDED(0),   /*Newly created config file*/
    VIR_DOMAIN_EVENT_DEFINED_UPDATED(1); /*Changed config file*/

    public final int id;

    private DomainEventDefinedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventDefinedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_DEFINED_ADDED;
	case 1:
	    return VIR_DOMAIN_EVENT_DEFINED_UPDATED;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventDefinedDetailType");
    }
}
