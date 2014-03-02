package org.libvirt.event;

/**
 * Domain Event Id.  This is a binding of 
 * <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainEventID">virDomainEventID</a>
 * 
 * @author Chris Ellis
 */
public enum DomainEventID {
    
    VIR_DOMAIN_EVENT_ID_LIFECYCLE(0),        /*virConnectDomainEventCallback*/
    VIR_DOMAIN_EVENT_ID_REBOOT(1),           /*virConnectDomainEventGenericCallback*/
    VIR_DOMAIN_EVENT_ID_RTC_CHANGE(2),       /*virConnectDomainEventRTCChangeCallback*/
    VIR_DOMAIN_EVENT_ID_WATCHDOG(3),         /*virConnectDomainEventWatchdogCallback*/
    VIR_DOMAIN_EVENT_ID_IO_ERROR(4),         /*virConnectDomainEventIOErrorCallback*/
    VIR_DOMAIN_EVENT_ID_GRAPHICS(5),         /*virConnectDomainEventGraphicsCallback*/
    VIR_DOMAIN_EVENT_ID_IO_ERROR_REASON(6),  /*virConnectDomainEventIOErrorReasonCallback*/
    VIR_DOMAIN_EVENT_ID_CONTROL_ERROR(7),    /*virConnectDomainEventGenericCallback*/
    VIR_DOMAIN_EVENT_ID_BLOCK_JOB(8),        /*virConnectDomainEventBlockJobCallback*/
    VIR_DOMAIN_EVENT_ID_DISK_CHANGE(9),      /*virConnectDomainEventDiskChangeCallback*/
    VIR_DOMAIN_EVENT_ID_TRAY_CHANGE(10),     /*virConnectDomainEventTrayChangeCallback*/
    VIR_DOMAIN_EVENT_ID_PMWAKEUP(11),        /*virConnectDomainEventPMWakeupCallback*/
    VIR_DOMAIN_EVENT_ID_PMSUSPEND(12),       /*virConnectDomainEventPMSuspendCallback*/
    VIR_DOMAIN_EVENT_ID_BALLOON_CHANGE(13),  /*virConnectDomainEventBalloonChangeCallback*/
    VIR_DOMAIN_EVENT_ID_PMSUSPEND_DISK(14),  /*virConnectDomainEventPMSuspendDiskCallback*/
    VIR_DOMAIN_EVENT_ID_DEVICE_REMOVED(15);  /*virConnectDomainEventDeviceRemovedCallback*/

    public final int id;

    private DomainEventID(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public static final DomainEventID valueOf(int id) {
	// could use a loop but switch for efficiency
	switch (id) {
	case 0: 
	    return VIR_DOMAIN_EVENT_ID_LIFECYCLE;
	case 1: 
	    return VIR_DOMAIN_EVENT_ID_REBOOT;
	case 2: 
	    return VIR_DOMAIN_EVENT_ID_RTC_CHANGE;
	case 3: 
	    return VIR_DOMAIN_EVENT_ID_WATCHDOG;
	case 4: 
	    return VIR_DOMAIN_EVENT_ID_IO_ERROR;
	case 5: 
	    return VIR_DOMAIN_EVENT_ID_GRAPHICS;
	case 6: 
	    return VIR_DOMAIN_EVENT_ID_IO_ERROR_REASON;
	case 7: 
	    return VIR_DOMAIN_EVENT_ID_CONTROL_ERROR;
	case 8: 
	    return VIR_DOMAIN_EVENT_ID_BLOCK_JOB;
	case 9: 
	    return VIR_DOMAIN_EVENT_ID_DISK_CHANGE;
	case 10: 
	    return VIR_DOMAIN_EVENT_ID_TRAY_CHANGE;
	case 11: 
	    return VIR_DOMAIN_EVENT_ID_PMWAKEUP;
	case 12: 
	    return VIR_DOMAIN_EVENT_ID_PMSUSPEND;
	case 13: 
	    return VIR_DOMAIN_EVENT_ID_BALLOON_CHANGE;
	case 14: 
	    return VIR_DOMAIN_EVENT_ID_PMSUSPEND_DISK;
	case 15: 
	    return VIR_DOMAIN_EVENT_ID_DEVICE_REMOVED;
	}
	throw new IllegalArgumentException(id + " is not a valid DomainEventID");
    }
}
