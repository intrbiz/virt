package org.libvirt;

import org.libvirt.jna.Libvirt;

import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents an instance of the JNA mapped libvirt
 * library.
 *
 * The library will get loaded when first accessing this class.
 *
 * Additionally, this class contains internal methods to ease
 * implementing the public API.
 */
public final class Library {
    private static AtomicBoolean runLoop = new AtomicBoolean();

    final static Libvirt libvirt;

    // an empty string array constant
    // prefer this over creating empty arrays dynamically.
    final static String[] NO_STRINGS = {};

    // Load the native part
    static {
        libvirt = Libvirt.INSTANCE;
        try {
            processError(libvirt.virInitialize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Library() {}

    /**
     * Returns the version of the native libvirt library.
     *
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     */
    public static long getVersion() throws LibvirtException {
        LongByReference libVer = new LongByReference();
        processError(libvirt.virGetVersion(libVer, null, null));
        return libVer.getValue();
    }

    /**
     * Free memory pointed to by ptr.
     */
    static void free(Pointer ptr) {
        libvirt.virFree(new PointerByReference(ptr));
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
     * Initialize the event loop.
     *
     * Registers a default event loop implementation based on the
     * poll() system call.
     * <p>
     * Once registered, the application has to invoke
     * { link #processEvent} in a loop or call { link #runEventLoop}
     * in another thread.
     * <p>
     * Note: You must call this function <em>before</em> connecting to
     *       the hypervisor.
     *
     * @throws LibvirtException on failure
     *
     * @see #processEvent
     * @see #runLoop
     */
    public static void initEventLoop() throws LibvirtException {
        processError(libvirt.virEventRegisterDefaultImpl());
    }

    /**
     * Run one iteration of the event loop.
     * <p>
     * Applications will generally want to have a thread which invokes
     * this method in an infinite loop:
     * <pre>
     * { code while (true) connection.processEvent(); }
     * </pre>
     * <p>
     * Failure to do so may result in connections being closed
     * unexpectedly as a result of keepalive timeout.
     *
     * @throws LibvirtException on failure
     *
     * @see #initEventLoop()
     */
    public static void processEvent() throws LibvirtException {
        // System.out.println("Enter process event");
        processError(libvirt.virEventRunDefaultImpl());
        // System.out.println("Exit process event");
    }

    /**
     * Runs the event loop.
     *
     * This method blocks until { link #stopEventLoop} is called or an
     * exception is thrown.
     * <p>
     * Usually, this method is run in another thread.
     *
     * @throws LibvirtException     if there was an error during the call of a
     *                              native libvirt function
     * @throws InterruptedException if this thread was interrupted by a call to
     *                              { link java.lang.Thread#interrupt() Thread.interrupt()}
     */
    public static void runEventLoop() throws LibvirtException, InterruptedException {
        runLoop.set(true);
        do {
            processEvent();
            if (Thread.interrupted())
                throw new InterruptedException();
        } while (runLoop.get());
    }

    /**
     * Stops the event loop.
     *
     * This methods stops an event loop when an event loop is
     * currently running, otherwise it does nothing.
     *
     * @see #runEventLoop
     */
    public static void stopEventLoop() throws LibvirtException {
        if (runLoop.getAndSet(false)) {
            // add a timeout which fires immediately so that the processEvent
            // method returns if it is waiting
            libvirt.virEventAddTimeout(0, new org.libvirt.jna.Libvirt.VirEventTimeoutCallback() {
                    @Override
                    public void tick(int id, Pointer p) {
                        // remove itself right after it served its purpose
                        libvirt.virEventRemoveTimeout(id);
                    }
                },
                null, null);
        }
    }

    /**
     * Look up a constant of an enum by its ordinal number.
     *
     * @return the corresponding enum constant when such a constant exists,
     *         otherwise the element which has the biggest ordinal number
     *         assigned.
     *
     * @throws IllegalArgumentException if { code ordinal} is negative
     */
    static <T extends Enum<T>> T getConstant(final Class<T> c, final int ordinal) {
        if (ordinal < 0)
            throw new IllegalArgumentException("ordinal must be >= 0");

        T[] a = c.getEnumConstants();

        assert a.length > 0 : "there must be at least one enum constant";

        return a[Math.min(ordinal, a.length - 1)];
    }
}
