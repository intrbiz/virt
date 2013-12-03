package com.intrbiz.virt.dash.image;

import java.security.SecureRandom;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.intrbiz.Util;
import com.intrbiz.virt.dash.cfg.VirtGuestImage;
import com.intrbiz.virt.dash.model.VirtHost;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.DiskDef;
import com.intrbiz.virt.libvirt.model.definition.InterfaceDef;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStoragePool;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStorageVol;

public class VirtGuestImager
{
    private Logger logger = Logger.getLogger(VirtGuestImager.class);

    protected final VirtHost host;

    protected final VirtGuestImage image;

    protected LibVirtDomainDef definition;

    protected String name;

    protected int cpuCount;

    protected long memory;

    protected String bridge;

    protected UUID storagePool;

    public VirtGuestImager(VirtHost host, VirtGuestImage image)
    {
        this.host = host;
        this.image = image;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getCpuCount()
    {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount)
    {
        this.cpuCount = cpuCount;
    }

    public long getMemory()
    {
        return memory;
    }

    public void setMemory(long memory)
    {
        this.memory = memory;
    }

    public String getBridge()
    {
        return bridge;
    }

    public void setBridge(String bridge)
    {
        this.bridge = bridge;
    }

    public UUID getStoragePool()
    {
        return storagePool;
    }

    public void setStoragePool(UUID storagePool)
    {
        this.storagePool = storagePool;
    }

    public void image()
    {
        try (LibVirtAdapter lv = LibVirtAdapter.connect(this.host.getUrl()))
        {
            this.setupDefinition(lv);
            this.setupBasics(lv);
            this.setupDisks(lv);
            this.setupInterfaces(lv);
            // create the domain
            this.logger.info("Creating domain with:\n" + this.definition.toString());
            LibVirtDomain dom = lv.addDomain(definition);
            this.logger.info("Created domain: " + dom.getName());
        }
    }

    protected void setupDefinition(LibVirtAdapter lv)
    {
        // clone the image def
        this.definition = LibVirtDomainDef.read(this.image.getDefinition().toString());
        // set name and uuid
        this.definition.setName(this.name);
        this.definition.setUuid(UUID.randomUUID().toString());
    }

    protected void setupBasics(LibVirtAdapter lv)
    {
        // setup basic config
        this.definition.getVcpu().setCount(this.cpuCount);
        this.definition.getMemory().setBytesValue(this.memory);
        this.definition.getCurrentMemory().setBytesValue(this.memory);
    }

    protected void setupDisks(LibVirtAdapter lv)
    {
        // setup the disks
        for (DiskDef ddef : this.definition.getDevices().getDisks())
        {
            if ("file".equals(ddef.getType()) && "disk".equals(ddef.getDevice()))
            {
                this.logger.info("Setting up disk " + ddef.getTarget().getDev());
                LibVirtStorageVol vol = lv.lookupStorageVolByPath(ddef.getSource().getFile());
                String volName = this.name + "_" + ddef.getTarget().getDev() + "." + ddef.getDriver().getType();
                // the pool to clone into
                LibVirtStoragePool into = this.storagePool == null ? vol.getStoragePool() : lv.lookupStoragePoolByUuid(this.storagePool);
                // clone the image
                LibVirtStorageVol clonedVol = vol.cloneFileVolume(volName, into);
                this.logger.info("Cloned vol: " + clonedVol.getPath() + " from " + vol.getPath());
                ddef.getSource().setFile(clonedVol.getPath());
            }
        }
    }

    protected void setupInterfaces(LibVirtAdapter lv)
    {
        for (InterfaceDef idef : this.definition.getDevices().getInterfaces())
        {
            String mac = this.randomMacAddress();
            idef.getMac().setAddress(mac);
            if ("bridge".equals(idef.getType()) && (!Util.isEmpty(this.bridge))) idef.getSource().setBridge(this.bridge);
            this.logger.info("Setting interface with MAC address: " + mac + " connected to bridge " + idef.getSource().getBridge());
        }
    }

    protected String randomMacAddress()
    {
        byte[] mac = new byte[3];
        new SecureRandom().nextBytes(mac);
        return "52:54:00:" + toHex(mac[0]) + ":" + toHex(mac[1]) + ":" + toHex(mac[2]);
    }

    protected static String toHex(byte b)
    {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) s = "0" + s;
        return s;
    }
}
