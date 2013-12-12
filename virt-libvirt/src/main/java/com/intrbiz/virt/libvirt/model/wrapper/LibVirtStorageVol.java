package com.intrbiz.virt.libvirt.model.wrapper;

import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;

import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.storage.LibVirtStorageVolumeDef;
import com.intrbiz.virt.libvirt.model.util.LibVirtCleanupWrapper;

public abstract class LibVirtStorageVol implements Comparable<LibVirtStorageVol>
{
    private final LibVirtAdapter adapter;

    private final StorageVol vol;

    private final String name;

    protected final LibVirtCleanupWrapper cleanup;

    public LibVirtStorageVol(LibVirtAdapter adapter, StorageVol vol)
    {
        this.adapter = adapter;
        this.vol = vol;
        this.cleanup = LibVirtCleanupWrapper.newStorageVolWrapper(vol);
        this.addStorageVolToCleanUp();
        //
        this.name = this.fetchName();
    }

    public LibVirtAdapter getLibVirtAdapter()
    {
        return this.adapter;
    }

    public StorageVol getLibVirtStorageVol()
    {
        return this.vol;
    }

    /**
     * Get the name of this guest (NB: this is cached)
     */
    public String getName()
    {
        return this.name;
    }

    protected String fetchName()
    {
        this.adapter.checkOpen();
        try
        {
            return this.vol.getName();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    protected String getKey()
    {
        this.adapter.checkOpen();
        try
        {
            return this.vol.getKey();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }
    
    public String getPath()
    {
        this.adapter.checkOpen();
        try
        {
            return this.vol.getKey();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }
    
    public void wipe()
    {
        this.adapter.checkOpen();
        try
        {
            this.vol.wipe();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }
    
    public void delete()
    {
        this.adapter.checkOpen();
        try
        {
            this.vol.delete(0);
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }
    
    public LibVirtStorageVolumeDef getStorageVolDef()
    {
        this.adapter.checkOpen();
        try
        {
            return LibVirtStorageVolumeDef.read(this.vol.getXMLDesc(0));
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to load storage vol XML definition", e);
        }
    }
    
    public LibVirtStoragePool getStoragePool()
    {
        this.adapter.checkOpen();
        try
        {
            return this.newStoragePool(this.vol.storagePoolLookupByVolume());
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to lookup storage pool", e);
        }
    }
    
    /**
     * Clone simple file based volumes
     * @param cloneName
     * @return
     */
    public LibVirtStorageVol cloneFileVolume(String cloneName, LibVirtStoragePool into)
    {
        this.adapter.checkOpen();
        // create the definition
        LibVirtStorageVolumeDef cloneVolDef = this.getStorageVolDef().clone();
        cloneVolDef.setName(cloneName);
        cloneVolDef.setKey(null);
        cloneVolDef.setAllocation(null);
        cloneVolDef.getTarget().setPath(null);
        // create the volume
        return into.addStorageVolFromExistingVolume(cloneVolDef, this);
    }
    
    public LibVirtStorageVol cloneFileVolume(String cloneName)
    {
        return this.cloneFileVolume(cloneName, this.getStoragePool());
    }

    @Override
    public void finalize()
    {
        synchronized (this)
        {
            this.cleanup.free();
            this.removeStorageVolFromCleanUp();
        }
    }

    @Override
    public int compareTo(LibVirtStorageVol o)
    {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LibVirtStorageVol other = (LibVirtStorageVol) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }

    protected abstract void addStorageVolToCleanUp();

    protected abstract void removeStorageVolFromCleanUp();
    
    protected abstract LibVirtStoragePool newStoragePool(StoragePool pool);
}
