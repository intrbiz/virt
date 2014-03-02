package org.libvirt;

/**
 * Handle errors thrown by event handlers which have not be handled
 * 
 * @Author Chris Ellis
 */
public class DomainEventUncaughtErrorHandler {

    public void uncaughtError(Throwable t) {
	// by default just print to std err
	t.printStackTrace();
    }
}
