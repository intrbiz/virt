package com.intrbiz.virt.libvirt.model.util;

import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;

/**
 * A wrapper to serialise access to .free() of libVirt object
 */
public abstract class LibVirtCleanupWrapper
{
    private volatile boolean freed = false;
    
    public void free()
    {
        synchronized (this)
        {
            System.out.println("Cleaning up libvirt object from Thread: " + Thread.currentThread().getName() + " freed: " + this.freed);
            if (! this.freed)
            {
                try
                {
                    this.freeObject();
                    this.freed = true;
                }
                catch (LibvirtException e)
                {
                }
            }
        }
    }
    
    protected abstract void freeObject() throws LibvirtException;
    
    public boolean isFreed()
    {
        return this.freed;
    }
    
    public static LibVirtCleanupWrapper newDomainWrapper(final Domain domain)
    {
        return new LibVirtCleanupWrapper() {
            protected void freeObject() throws LibvirtException
            {
                domain.free();
            }
        };
    }
    
    public static LibVirtCleanupWrapper newStoragePoolWrapper(final StoragePool pool)
    {
        return new LibVirtCleanupWrapper() {
            protected void freeObject() throws LibvirtException
            {
                pool.free();
            }
        };
    }
    
    public static LibVirtCleanupWrapper newStorageVolWrapper(final StorageVol vol)
    {
        return new LibVirtCleanupWrapper() {
            protected void freeObject() throws LibvirtException
            {
                vol.free();
            }
        };
    }
}
