package com.intrbiz.virt.dash.poller;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.dash.model.VirtGuest;
import com.intrbiz.virt.dash.model.VirtGuest.GuestState;
import com.intrbiz.virt.dash.model.VirtGuestDisk;
import com.intrbiz.virt.dash.model.VirtGuestInterface;
import com.intrbiz.virt.dash.model.VirtHost;
import com.intrbiz.virt.dash.model.VirtStoragePool;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.DiskDef;
import com.intrbiz.virt.libvirt.model.definition.GraphicsDef;
import com.intrbiz.virt.libvirt.model.definition.InterfaceDef;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtHostInterface;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStoragePool;

public class LibVirtPoller implements Runnable
{
    private Logger logger = Logger.getLogger(LibVirtPoller.class);

    private long sleepTime = 30_000;

    private final App virtDash;
    
    private Thread thread;

    public LibVirtPoller(App app)
    {
        this.virtDash = app;
    }

    public long getSleepTime()
    {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime)
    {
        this.sleepTime = sleepTime;
    }

    public App getVirtDash()
    {
        return virtDash;
    }

    public void run()
    {
        while (true)
        {
            for (VirtHost host : this.virtDash.getHosts())
            {
                this.pollHost(host);
            }
            this.virtDash.writeWebsockifyConfig();
            sleep();
        }
    }
    
    public void pollHost(VirtHost host)
    {
        logger.trace("Polling host " + host.getName() + " " + host.getUrl());
        //
        try (LibVirtAdapter lv = LibVirtAdapter.connect(host.getUrl()))
        {
            // update the host information
            LibVirtNodeInfo hostInfo = lv.nodeInfo();
            host.setArch(hostInfo.getModel());
            host.setCpuCount(hostInfo.getCpus());
            host.setCpuSpeed(hostInfo.getMhz());
            host.setMemory(hostInfo.getMemory());
            // update the guests
            long definedMemory = 0;
            Set<String> domains = new HashSet<String>();
            for (LibVirtDomain domain : lv.listDomains())
            {
                domains.add(domain.getName());
                VirtGuest guest = host.getGuest(domain.getName());
                if (guest == null) guest = host.addGuest(new VirtGuest(domain.getName(), domain.getUUID()));
                LibVirtDomainDef def = domain.getDomainDef();
                guest.setCpuCount(def.getVcpu().getCount());
                guest.setMemory(def.getMemory().getBytesValue());
                definedMemory += guest.getMemory();
                guest.setDefinition(def);
                guest.setState(domain.isRunning() ? GuestState.RUNNING : GuestState.DEFINED);
                // vnc
                for (GraphicsDef gfx : def.getDevices().getGraphics())
                {
                    if ("vnc".equals(gfx.getType()))
                    {
                        guest.setVncPort(gfx.getPort());
                    }
                }
                // disks
                guest.getDisks().clear();
                for (DiskDef dsk : def.getDevices().getDisks())
                {
                    VirtGuestDisk disk = new VirtGuestDisk();
                    disk.setTargetBus(dsk.getTarget().getBus());
                    disk.setTargetDevice(dsk.getTarget().getDev());
                    disk.setDriverName(dsk.getDriver().getName());
                    disk.setDriverType(dsk.getDriver().getType());
                    if (dsk.getSource() != null) disk.setSourceUrl(dsk.getSource().getFile());
                    disk.setType(dsk.getType());
                    disk.setDevice(dsk.getDevice());
                    guest.addDisk(disk);
                }
                // interfaces
                guest.getInterfaces().clear();
                for (InterfaceDef idef : def.getDevices().getInterfaces())
                {
                    VirtGuestInterface iface = new VirtGuestInterface();
                    iface.setMacAddress(idef.getMac().getAddress());
                    iface.setType(idef.getType());
                    if ("bridge".equals(iface.getType()) && idef.getSource() != null) iface.setBridge(idef.getSource().getBridge());
                    guest.addInterface(iface);
                }
            }
            // remove any guests which have been undefined
            for (VirtGuest guest : host.getGuests())
            {
                if (! domains.contains(guest.getName()))
                    host.removeGuest(guest.getName());
            }
            // host interfaces
            host.getBridges().clear();
            for (LibVirtHostInterface hif : lv.listHostInterfaces())
            {
                host.addBridge(hif.getName());
            }
            // host storage
            host.getStoragePools().clear();
            for (LibVirtStoragePool pool : lv.listStoragePools())
            {
                VirtStoragePool vsp = new VirtStoragePool();
                vsp.setName(pool.getName());
                vsp.setUuid(pool.getUUID());
                vsp.setType(pool.getStoragePoolDef().getType());
                host.addStoragePool(vsp);
            }
            // set the defined memory count
            host.setDefinedMemory(definedMemory);
            host.setUp(true);
        }
        catch (Exception e)
        {
            host.setUp(false);
            logger.warn("Error polling host", e);
        }
    }
    
    protected void sleep()
    {
        try
        {
            Thread.sleep(this.sleepTime);
        }
        catch (InterruptedException e)
        {
        }
    }
    
    public void start()
    {
        if (this.thread == null)
        {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }
}
