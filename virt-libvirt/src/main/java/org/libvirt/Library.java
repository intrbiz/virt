package org.libvirt;

import org.libvirt.jna.Libvirt;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * This class represents an instance of the JNA mapped libvirt
 * library.
 *
 * The library will get loaded when first accessing this class.
 * By default the Libvirt event loop will be setup and laucnhed 
 * in a separate thread, this is to permit the usage of DomainEvents.
 *
 * Additionally, this class contains internal methods to ease
 * implementing the public API.
 */
final class Library {
    final static Libvirt libvirt;
    
    static EventLoop eventloop; 

    // an empty string array constant
    // prefer this over creating empty arrays dynamically.
    final static String[] NO_STRINGS = {};

    // Load the native part
    static {
        Libvirt.INSTANCE.virInitialize();
        libvirt = Libvirt.INSTANCE;
        try {
            ErrorHandler.processError(Libvirt.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // by default start the event loop
        startEventLoop();
    }

    private Library() {}
    
    /**
     * Setup and start the Libvirt event loop,
     * the event loop is required to use domain 
     * events
     */
    static synchronized void startEventLoop() {
        try {
            if (eventloop == null) {
                // setup the event loop
                if (libvirt.virEventRegisterDefaultImpl() < 0) {
                    ErrorHandler.processError(libvirt);
                }
                // start the event loop
                (eventloop = new EventLoop()).start();
            }
        }
        catch (Exception e) {
            System.err.println("Error setting up event loop:");
            e.printStackTrace();
        }
    }
    
    /**
     * Shutdown the Libvirt event loop
     */
    static synchronized void shutdownEventLoop() {

        if (eventloop != null) {
            eventloop.shutdown();
        }
        eventloop = null;
    }

    /**
     * Free memory pointed to by ptr.
     */
    static void free(Pointer ptr) {
        Native.free(Pointer.nativeValue(ptr));
        Pointer.nativeValue(ptr, 0L);
    }

    /**
     * Convert the data pointed to by {@code ptr} to a String.
     */
    static String getString(Pointer ptr) {
        final long len = ptr.indexOf(0, (byte)0);
        assert (len != -1): "C-Strings must be \\0 terminated.";

        final byte[] data = ptr.getByteArray(0, (int)len);
        try {
            return new String(data, "utf-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Libvirt problem: UTF-8 decoding error.", e);
        }
    }

    /**
     * Calls {@link #toStringArray(Pointer[], int)}.
     */
    static String[] toStringArray(Pointer[] ptrArr) {
        return toStringArray(ptrArr, ptrArr.length);
    }

    /**
     * Convert the given array of native pointers to "char" in
     * UTF-8 encoding to an array of Strings.
     *
     * \note The memory used by the elements of the original array
     *       is freed and ptrArr is modified.
     */
    static String[] toStringArray(Pointer[] ptrArr, final int size) {
        try {
            final String[] result = new String[size];
            for (int i = 0; i < size; ++i) {
                result[i] = Library.getString(ptrArr[i]);
            }
            return result;
        } finally {
            for (int i = 0; i < size; ++i) {
                Library.free(ptrArr[i]);
                ptrArr[i] = null;
            }
        }
    }
    
    /**
     * Execute the Libvirt event loop to process events 
     * and dispatch callbacks
     */
    static class EventLoop implements Runnable {
        
        private volatile boolean run = true;
        
        public void run() {
            Libvirt lv = libvirt;
            while (this.run) {
                try {
                    if (lv.virEventRunDefaultImpl() < 0) {
                        // should we bomb the run loop?
                        ErrorHandler.processError(lv);
                    }
                }
                catch (LibvirtException e) {
                    System.err.println("Error processing libvirt event loop");
                    e.printStackTrace();
                }
            }
        }
        
        public void start() {
            Thread t = new Thread(this, "Libvirt Event Loop");
            t.start();
        }
        
        public void shutdown() {
            this.run = false;
        }
    }
}
