package com.intrbiz.virt.libvirt.model.wrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;

import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.storage.LibVirtStoragePoolDef;
import com.intrbiz.virt.libvirt.model.definition.storage.LibVirtStorageVolumeDef;
import com.intrbiz.virt.libvirt.model.util.LibVirtCleanupWrapper;

public abstract class LibVirtStoragePool implements Comparable<LibVirtStoragePool>
{
    private final LibVirtAdapter adapter;

    private final StoragePool pool;

    private final String name;

    private final UUID uuid;

    protected final LibVirtCleanupWrapper cleanup;

    public LibVirtStoragePool(LibVirtAdapter adapter, StoragePool pool)
    {
        this.adapter = adapter;
        this.pool = pool;
        this.cleanup = LibVirtCleanupWrapper.newStoragePoolWrapper(this.pool);
        this.addStoragePoolToCleanUp();
        //
        this.name = this.fetchName();
        this.uuid = this.fetchUUID();
    }

    public LibVirtAdapter getLibVirtAdapter()
    {
        return this.adapter;
    }

    public StoragePool getLibVirtStoragePool()
    {
        return this.pool;
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
            return this.pool.getName();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    /**
     * Get the UUID of this guest (NB: this is cached)
     * 
     * @return
     */
    public UUID getUUID()
    {
        return this.uuid;
    }

    protected UUID fetchUUID()
    {
        this.adapter.checkOpen();
        try
        {
            return UUID.fromString(this.pool.getUUIDString());
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    public boolean isRunning()
    {
        this.adapter.checkOpen();
        try
        {
            return this.pool.getAutostart();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine storage pool is active",e);
        }
    }

    public boolean isAutostart()
    {
        this.adapter.checkOpen();
        try
        {
            return this.pool.getAutostart();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine storage pool autostart",e);
        }
    }

    public void configureAutostart(boolean autostart)
    {
        this.adapter.checkOpen();
        try
        {
            this.pool.setAutostart(autostart ? 1 : 0);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to configure storage pool", e);
        }
    }
    
    public void build()
    {
        this.adapter.checkOpen();
        try
        {
            this.pool.build(0);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to build storage pool",e);
        }
    }
    
    public void create()
    {
        this.adapter.checkOpen();
        try
        {
            this.pool.create(0);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to create storage pool",e);
        }
    }
    
    public void delete()
    {
        this.adapter.checkOpen();
        try
        {
            this.pool.delete(0);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to delete storage pool",e);
        }
    }
    
    public void stop()
    {
        this.adapter.checkOpen();
        try
        {
            this.pool.destroy();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to destroy storage pool",e);
        }
    }

    public LibVirtStoragePoolDef getStoragePoolDef()
    {
        this.adapter.checkOpen();
        try
        {
            return LibVirtStoragePoolDef.read(this.pool.getXMLDesc(0));
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to load storage pool XML definition", e);
        }
    }

    public List<LibVirtStorageVol> listVolumes()
    {
        this.adapter.checkOpen();
        List<LibVirtStorageVol> l = new LinkedList<LibVirtStorageVol>();
        try
        {
            for (String vol : this.pool.listVolumes())
            {
                l.add(this.newLibVirtStorageVol(this.pool.storageVolLookupByName(vol)));
            }
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to list storage volumes", e);
        }
        Collections.sort(l);
        return l;
    }

    public LibVirtStorageVol lookupStorageVolByName(String name)
    {
        this.adapter.checkOpen();
        try
        {
            return this.newLibVirtStorageVol(this.pool.storageVolLookupByName(name));
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to lookup storage volume", e);
        }
    }
    
    public LibVirtStorageVol addStorageVol(LibVirtStorageVolumeDef def)
    {
        this.adapter.checkOpen();
        try
        {
            return this.newLibVirtStorageVol(this.pool.storageVolCreateXML(def.toString(), 0));
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to add storage volume", e);
        }
    }
    
    public LibVirtStorageVol addStorageVolFromExistingVolume(LibVirtStorageVolumeDef def, LibVirtStorageVol volume)
    {
        this.adapter.checkOpen();
        try
        {
            return this.newLibVirtStorageVol(this.pool.storageVolCreateXMLFrom(def.toString(), volume.getLibVirtStorageVol(), 0));
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to add storage volume", e);
        }
    }

    @Override
    public void finalize()
    {
        synchronized (this)
        {
            this.cleanup.free();
            this.removeStoragePoolFromCleanUp();
        }
    }

    @Override
    public int compareTo(LibVirtStoragePool o)
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
        LibVirtStoragePool other = (LibVirtStoragePool) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }

    protected abstract void addStoragePoolToCleanUp();

    protected abstract void removeStoragePoolFromCleanUp();

    protected abstract LibVirtStorageVol newLibVirtStorageVol(StorageVol vol);
}
