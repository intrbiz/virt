package com.intrbiz.virt.libvirt.model.wrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.libvirt.Domain;
import org.libvirt.DomainBlockInfo;
import org.libvirt.DomainBlockStats;
import org.libvirt.DomainInterfaceStats;
import org.libvirt.LibvirtException;

import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.LibVirtEventHandler;
import com.intrbiz.virt.libvirt.event.LibVirtDomainLifecycleEventHandler;
import com.intrbiz.virt.libvirt.event.LibVirtDomainRebootEventHandler;
import com.intrbiz.virt.libvirt.model.definition.DiskDef;
import com.intrbiz.virt.libvirt.model.definition.InterfaceDef;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.util.LibVirtCleanupWrapper;

/**
 * A guest virtual machine defined or running on a host
 */
public abstract class LibVirtDomain implements Comparable<LibVirtDomain>
{
    protected final LibVirtAdapter adapter;

    protected final Domain domain;

    protected final String name;

    protected final UUID uuid;

    protected final LibVirtCleanupWrapper cleanup;

    public LibVirtDomain(LibVirtAdapter adapter, Domain domain)
    {
        this.adapter = adapter;
        this.domain = domain;
        this.cleanup = LibVirtCleanupWrapper.newDomainWrapper(this.domain);
        this.name = this.fetchName();
        this.uuid = this.fetchUUID();
        this.addDomainToCleanUp();
    }

    public LibVirtAdapter getLibVirtAdapter()
    {
        return this.adapter;
    }

    public Domain getLibVirtDomain()
    {
        return this.domain;
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
            return this.domain.getName();
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
            return UUID.fromString(this.domain.getUUIDString());
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    /**
     * Get the runtime id of this guest
     */
    public int getId()
    {
        this.adapter.checkOpen();
        try
        {
            return this.domain.getID();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    /**
     * Is this guest set to start when the host starts
     */
    public boolean isAutostart()
    {
        this.adapter.checkOpen();
        try
        {
            return this.domain.getAutostart();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine VM autostart");
        }
    }

    /**
     * Is this guest running
     */
    public boolean isRunning()
    {
        this.adapter.checkOpen();
        try
        {
            return this.domain.isActive() == 1;
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine if VM is running");
        }
    }

    /**
     * Is this guest persistent
     */
    public boolean isPersistent()
    {
        this.adapter.checkOpen();
        try
        {
            return this.domain.isPersistent() == 1;
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine if VM is persistent");
        }
    }

    /**
     * Get and parse the XML definition of this guest
     */
    public LibVirtDomainDef getDomainDef()
    {
        this.adapter.checkOpen();
        try
        {
            return LibVirtDomainDef.read(this.domain.getXMLDesc(1));
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to load VM XML definition", e);
        }
    }

    /**
     * Start this guest
     */
    public void start()
    {
        this.adapter.checkOpen();
        try
        {
            if (this.domain.isActive() != 1) this.domain.create();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to create domain", e);
        }
    }

    /**
     * Send the guest the shutdown signal, note some guests might ignore this.
     */
    public void powerOff()
    {
        this.adapter.checkOpen();
        try
        {
            if (this.domain.isActive() == 1) this.domain.shutdown();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to shutdown domain", e);
        }
    }

    /**
     * Terminate this guest immediately, note this does not cleanly shutdown the guest
     */
    public void terminate()
    {
        this.adapter.checkOpen();
        try
        {
            if (this.domain.isActive() == 1) this.domain.destroy();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to destory domain", e);
        }
    }

    /**
     * Remove this guest definition from the host, terminating the guest if it is running
     */
    public void remove()
    {
        this.adapter.checkOpen();
        try
        {
            if (this.domain.isActive() == 1) this.domain.destroy();
            this.domain.undefine();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to remove domain", e);
        }
    }

    /**
     * Enable or disable the guest starting when the host starts
     */
    public void configureAutostart(boolean autostart)
    {
        this.adapter.checkOpen();
        try
        {
            this.domain.setAutostart(autostart);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to configure domain", e);
        }
    }

    public LibVirtDiskInfo getDiskInfo(String device)
    {
        this.adapter.checkOpen();
        try
        {
            DomainBlockInfo info = this.domain.blockInfo(device);
            return new LibVirtDiskInfo(info.getCapacity(), info.getAllocation(), info.getPhysical());
        }
        catch (LibvirtException e)
        {
            // ignore
        }
        return null;
    }

    public LibVirtDiskInfo getDiskInfo(LibVirtDisk disk)
    {
        this.adapter.checkOpen();
        return this.getDiskInfo(disk.getTargetName());
    }

    public LibVirtDiskStats getDiskStats(String dev)
    {
        this.adapter.checkOpen();
        if (dev == null) return null;
        if (!this.isRunning()) return null;
        try
        {
            DomainBlockStats stats = this.domain.blockStats(dev);
            return new LibVirtDiskStats(stats.rd_req, stats.rd_bytes, stats.wr_req, stats.wr_bytes, stats.errs);
        }
        catch (LibvirtException e)
        {
            // ignore
        }
        return null;
    }

    public LibVirtDiskStats getDiskStats(LibVirtDisk disk)
    {
        this.adapter.checkOpen();
        return this.getDiskStats(disk.getTargetName());
    }

    /**
     * Get the block devices of this guest
     */
    public List<LibVirtDisk> getDisks()
    {
        this.adapter.checkOpen();
        List<LibVirtDisk> disks = new LinkedList<LibVirtDisk>();
        LibVirtDomainDef def = this.getDomainDef();
        for (DiskDef dskDef : def.getDevices().getDisks())
        {
            disks.add(new LibVirtDisk(dskDef)
            {
                @Override
                public LibVirtDiskInfo getDiskInfo()
                {
                    if (this.getSourceUrl() == null) return null;
                    return LibVirtDomain.this.getDiskInfo(this);
                }

                @Override
                public LibVirtDiskStats getDiskStats()
                {
                    if (this.getSourceUrl() == null) return null;
                    return LibVirtDomain.this.getDiskStats(this);
                }
            });
        }
        return disks;
    }

    public LibVirtInterfaceStats getInterfaceStats(String path)
    {
        this.adapter.checkOpen();
        if (path == null) return null;
        try
        {
            DomainInterfaceStats stats = this.domain.interfaceStats(path);
            return new LibVirtInterfaceStats(stats.rx_bytes, stats.rx_packets, stats.rx_errs, stats.rx_drop, stats.tx_bytes, stats.tx_packets, stats.tx_errs, stats.tx_drop);
        }
        catch (LibvirtException e)
        {
            // ignore
        }
        return null;
    }

    public LibVirtInterfaceStats getInterfaceStats(LibVirtInterface iface)
    {
        this.adapter.checkOpen();
        return this.getInterfaceStats(iface.getName());
    }

    /**
     * Get the interfaces of this guest
     */
    public List<LibVirtInterface> getInterfaces()
    {
        this.adapter.checkOpen();
        List<LibVirtInterface> ifaces = new LinkedList<LibVirtInterface>();
        LibVirtDomainDef def = this.getDomainDef();
        for (InterfaceDef ifDef : def.getDevices().getInterfaces())
        {
            ifaces.add(new LibVirtInterface(ifDef)
            {
                @Override
                public LibVirtInterfaceStats getInterfaceStats()
                {
                    return LibVirtDomain.this.getInterfaceStats(this);
                }
            });
        }
        return ifaces;
    }

    public LibVirtDomainLifecycleEventHandler registerLifecycleEventHandler(LibVirtDomainLifecycleEventHandler handler)
    {
        return this.adapter.registerEventHandler(this, handler);
    }

    public LibVirtDomainRebootEventHandler registerRebootEventHandler(LibVirtDomainRebootEventHandler handler)
    {
        return this.adapter.registerEventHandler(this, handler);
    }

    public <T extends LibVirtEventHandler<?>> T registerEventHandler(T handler)
    {
        return this.adapter.registerEventHandler(this, handler);
    }

    @Override
    public int compareTo(LibVirtDomain o)
    {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode()
    {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LibVirtDomain other = (LibVirtDomain) obj;
        return this.uuid.equals(other.uuid);
    }

    @Override
    public void finalize()
    {
        synchronized (this)
        {
            this.cleanup.free();
            this.removeDomainFromCleanUp();
        }
    }

    protected abstract void addDomainToCleanUp();

    protected abstract void removeDomainFromCleanUp();
}
