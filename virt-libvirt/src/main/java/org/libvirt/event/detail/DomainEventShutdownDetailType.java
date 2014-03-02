package org.libvirt.event.detail;

/**
 * Detail of a domain shutdown event.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventShutdownDetailType">virDomainEventShutdownDetailType</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventShutdownDetailType {
    
    VIR_DOMAIN_EVENT_SHUTDOWN_FINISHED(0);   /*Guest finished shutdown sequence*/

    public final int id;

    private DomainEventShutdownDetailType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventShutdownDetailType valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0:
	    return VIR_DOMAIN_EVENT_SHUTDOWN_FINISHED;
	}
	throw new IllegalArgumentException(
		id + " is not a valid DomainEventShutdownDetailType");
    }
}
