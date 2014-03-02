package org.libvirt.event.detail;

/**
 * Detail of a domain PM suspended event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventPMSuspendedDetailType">virDomainEventPMSuspendedDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventPMSuspendedDetailType {
    
    VIR_DOMAIN_EVENT_PMSUSPENDED_MEMORY(1),   /*Guest was PM suspended to memory*/
    VIR_DOMAIN_EVENT_PMSUSPENDED_DISK(2);   /*Guest was PM suspended to disk*/

    public final int id;

    private DomainEventPMSuspendedDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventPMSuspendedDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_PMSUSPENDED_MEMORY;
	case 1:
	    return VIR_DOMAIN_EVENT_PMSUSPENDED_DISK;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventPMSuspendedDetailType");
    }
}
