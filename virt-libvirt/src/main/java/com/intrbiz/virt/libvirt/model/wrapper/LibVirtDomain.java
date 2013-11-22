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
import com.intrbiz.virt.libvirt.model.definition.DiskDef;
import com.intrbiz.virt.libvirt.model.definition.InterfaceDef;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;

public class LibVirtDomain implements Comparable<LibVirtDomain>, AutoCloseable
{
    private final LibVirtAdapter adapter;

    private final Domain domain;

    public LibVirtDomain(LibVirtAdapter adapter, Domain domain)
    {
        this.adapter = adapter;
        this.domain = domain;
        this.adapter.addDomainToCleanUp(this);
    }

    public LibVirtAdapter getLibVirtAdapter()
    {
        return this.adapter;
    }

    public Domain getLibVirtDomain()
    {
        return this.domain;
    }

    public String getName()
    {
        try
        {
            return this.domain.getName();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    public UUID getUUID()
    {
        try
        {
            return UUID.fromString(this.domain.getUUIDString());
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    public int getId()
    {
        try
        {
            return this.domain.getID();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    public boolean isAutostart()
    {
        try
        {
            return this.domain.getAutostart();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine VM autostart");
        }
    }

    public boolean isRunning()
    {
        try
        {
            return this.domain.isActive() == 1;
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine if VM is running");
        }
    }

    public boolean isPersistent()
    {
        try
        {
            return this.domain.isPersistent() == 1;
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to determine if VM is persistent");
        }
    }

    public LibVirtDomainDef getDomainDef()
    {
        try
        {
            return LibVirtDomainDef.read(this.domain.getXMLDesc(1));
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to load VM XML definition", e);
        }
    }

    public void start()
    {
        try
        {
            if (this.domain.isActive() != 1) this.domain.create();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to create domain", e);
        }
    }

    public void powerOff()
    {
        try
        {
            if (this.domain.isActive() == 1) this.domain.shutdown();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to shutdown domain", e);
        }
    }

    public void terminate()
    {
        try
        {
            if (this.domain.isActive() == 1) this.domain.destroy();
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to destory domain", e);
        }
    }

    public void remove()
    {
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

    public void configureAutostart(boolean autostart)
    {
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
        return this.getDiskInfo(disk.getTargetName());
    }

    public LibVirtDiskStats getDiskStats(String dev)
    {
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
        return this.getDiskStats(disk.getTargetName());
    }

    public List<LibVirtDisk> getDisks()
    {
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
        return this.getInterfaceStats(iface.getName());
    }

    public List<LibVirtInterface> getInterfaces()
    {
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

    @Override
    public int compareTo(LibVirtDomain o)
    {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public int hashCode()
    {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LibVirtDomain other = (LibVirtDomain) obj;
        return this.getUUID().equals(other.getUUID());
    }

    /**
     * Free this domain
     */
    public void close()
    {
        try
        {
            this.domain.free();
        }
        catch (LibvirtException e)
        {
            e.printStackTrace();
        }
        this.adapter.removeDomainToCleanUp(this);
    }
}
