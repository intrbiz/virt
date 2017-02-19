package com.intrbiz.virt.dash.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.intrbiz.virt.dash.cfg.VirtGuestImage;
import com.intrbiz.virt.dash.model.VirtGuest.GuestState;
import com.intrbiz.virt.libvirt.CloseListener;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.event.LibVirtDomainLifecycleEventHandler;
import com.intrbiz.virt.libvirt.model.definition.DiskDef;
import com.intrbiz.virt.libvirt.model.definition.GraphicsDef;
import com.intrbiz.virt.libvirt.model.definition.InterfaceDef;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.event.LibVirtDomainLifecycle;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainCrashed;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainDefined;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainPMSuspended;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainResumed;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainShutdown;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainStarted;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainStopped;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainSuspended;
import com.intrbiz.virt.libvirt.model.event.lifecycle.LibVirtDomainUndefined;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtHostInterface;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStoragePool;

public class VirtHost implements Comparable<VirtHost>
{
    private Logger logger = Logger.getLogger(VirtHost.class);
    
    private String name;

    private String address;

    private String url;

    private String arch;

    private int cpuCount;

    private int cpuSpeed;

    private long memory;

    private long definedMemory;

    private ConcurrentMap<String, VirtGuest> guests = new ConcurrentHashMap<String, VirtGuest>();

    private boolean up = false;

    private List<VirtGuestImage> images = new LinkedList<VirtGuestImage>();

    private List<String> bridges = new LinkedList<String>();

    private List<VirtStoragePool> storagePools = new LinkedList<VirtStoragePool>();

    private LibVirtAdapter connection;
    
    private Timer timer = new Timer();

    public VirtHost()
    {
        super();
    }

    public VirtHost(String name, String address, String url)
    {
        super();
        this.name = name;
        this.address = address;
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getArch()
    {
        return arch;
    }

    public void setArch(String arch)
    {
        this.arch = arch;
    }

    public int getCpuCount()
    {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount)
    {
        this.cpuCount = cpuCount;
    }

    public int getCpuSpeed()
    {
        return cpuSpeed;
    }

    public void setCpuSpeed(int cpuSpeed)
    {
        this.cpuSpeed = cpuSpeed;
    }

    public long getMemory()
    {
        return memory;
    }

    public void setMemory(long memory)
    {
        this.memory = memory;
    }

    public long getDefinedMemory()
    {
        return definedMemory;
    }

    public void setDefinedMemory(long definedMemory)
    {
        this.definedMemory = definedMemory;
    }

    public long getAvailableMemory()
    {
        return this.memory - this.definedMemory;
    }

    public boolean isUp()
    {
        return up;
    }

    public void setUp(boolean up)
    {
        this.up = up;
    }

    public List<VirtGuestImage> getImages()
    {
        return images;
    }

    public void setImages(List<VirtGuestImage> images)
    {
        this.images = images;
    }

    public List<VirtGuest> getGuests()
    {
        List<VirtGuest> l = new LinkedList<VirtGuest>();
        l.addAll(guests.values());
        Collections.sort(l);
        return l;
    }

    public VirtGuest addGuest(VirtGuest guest)
    {
        this.guests.put(guest.getName(), guest);
        return guest;
    }

    public void removeGuest(String name)
    {
        this.guests.remove(name);
    }

    public VirtGuest getGuest(String name)
    {
        return this.guests.get(name);
    }

    public VirtGuestImage getImage(String name)
    {
        for (VirtGuestImage img : this.images)
        {
            if (name.equals(img.getName())) return img;
        }
        return null;
    }

    public List<String> getBridges()
    {
        return bridges;
    }

    public void addBridge(String name)
    {
        this.bridges.add(name);
        Collections.sort(this.bridges);
    }

    public List<VirtStoragePool> getStoragePools()
    {
        return storagePools;
    }

    public void addStoragePool(VirtStoragePool storagePool)
    {
        this.storagePools.add(storagePool);
        Collections.sort(this.storagePools);
    }
    
    //
    
    public LibVirtAdapter getConnection()
    {
        return connection;
    }

    /**
     * Connect to the LibVirt daemon on the host
     * and do the initial setup
     */
    public void connect()
    {
        logger.trace("Connecting to host " + this.getName() + " " + this.getUrl());
        try
        {
            this.connection = LibVirtAdapter.connect(this.getUrl());
            // scan the host
            this.scan();
            // setup listener
            this.listen();
            // add a close listener to reconnect
            this.connection.addCloseListener(new CloseListener() {
                @Override
                public void onClose(LibVirtAdapter adapter)
                {
                    // schedule reconnect
                    logger.info("Scheduling reconnect to libvirt");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run()
                        {
                            connect();
                        }
                    }, 
                    30_000L);
                    connection = null;
                }                
            });
        }
        catch (Exception e)
        {
            this.up = false;
            logger.warn("Error connecting to host", e);
            // schedule reconnect
            logger.info("Scheduling reconnect to libvirt");
            timer.schedule(new TimerTask() {
                @Override
                public void run()
                {
                    connect();
                }
            }, 
            30_000L);
        }
    }
    
    /**
     * Setup listeners to listen for domain events
     */
    private void listen()
    {
        if (this.connection != null && this.connection.isConnected() && this.connection.isAlive())
        {
            this.connection.registerLifecycleEventHandler(new LibVirtDomainLifecycleEventHandler() {
                @Override
                public void onEvent(LibVirtDomainLifecycle event)
                {
                    logger.info("Got domain event: " + event + " " + event.getDomain());
                    if (event instanceof LibVirtDomainDefined)
                    {
                        // domain added
                        VirtGuest guest = getGuest(event.getDomain().getName());
                        if (guest == null) guest = addGuest(new VirtGuest(event.getDomain().getName(), event.getDomain().getUUID()));
                        updateGuest(guest, event.getDomain());
                    }
                    else if (event instanceof LibVirtDomainUndefined)
                    {
                        // domain removed
                        guests.remove(event.getDomain().getName());
                    }
                    else if (event instanceof LibVirtDomainStopped || event instanceof LibVirtDomainCrashed)
                    {
                        VirtGuest guest = getGuest(event.getDomain().getName());
                        if (guest != null) guest.setState(GuestState.DEFINED);
                    }
                    else if (event instanceof LibVirtDomainShutdown)
                    {
                        VirtGuest guest = getGuest(event.getDomain().getName());
                        if (guest != null) guest.setState(GuestState.STOPPING);
                    }
                    else if (event instanceof LibVirtDomainStarted)
                    {
                        VirtGuest guest = getGuest(event.getDomain().getName());
                        if (guest != null) guest.setState(GuestState.RUNNING);
                    }
                    else if (event instanceof LibVirtDomainSuspended || event instanceof LibVirtDomainPMSuspended)
                    {
                        VirtGuest guest = getGuest(event.getDomain().getName());
                        if (guest != null) guest.setState(GuestState.DEFINED);
                    }
                    else if (event instanceof LibVirtDomainResumed)
                    {
                        VirtGuest guest = getGuest(event.getDomain().getName());
                        if (guest != null) guest.setState(GuestState.RUNNING);
                    }
                    // update stats
                    // compute defined memory
                    setDefinedMemory(computeRunningMemory());
                }
            });
        }
        else
        {
            logger.warn("Not registered listeners, connection is dead");
        }
    }
    
    /**
     * Perform a scan of the host
     */
    private void scan()
    {
        if (this.connection != null && this.connection.isConnected() && this.connection.isAlive())
        {
            // update the host information
            LibVirtNodeInfo hostInfo = this.connection.nodeInfo();
            this.setArch(hostInfo.getModel());
            this.setCpuCount(hostInfo.getCpus());
            this.setCpuSpeed(hostInfo.getMhz());
            this.setMemory(hostInfo.getMemory());
            // update the guests
            Set<String> domains = new HashSet<String>();
            for (LibVirtDomain domain : this.connection.listDomains())
            {
                domains.add(domain.getName());
                VirtGuest guest = this.getGuest(domain.getName());
                if (guest == null) guest = this.addGuest(new VirtGuest(domain.getName(), domain.getUUID()));
                this.updateGuest(guest, domain);
            }
            // remove any guests which have been undefined
            for (VirtGuest guest : this.getGuests())
            {
                if (! domains.contains(guest.getName()))
                    this.removeGuest(guest.getName());
            }
            // host interfaces
            this.getBridges().clear();
            for (LibVirtHostInterface hif : this.connection.listHostInterfaces())
            {
                this.addBridge(hif.getName());
            }
            // host storage
            this.getStoragePools().clear();
            for (LibVirtStoragePool pool : this.connection.listStoragePools())
            {
                VirtStoragePool vsp = new VirtStoragePool();
                vsp.setName(pool.getName());
                vsp.setUuid(pool.getUUID());
                vsp.setType(pool.getStoragePoolDef().getType());
                this.addStoragePool(vsp);
            }
            // compute defined memory
            this.setDefinedMemory(this.computeRunningMemory());
            // we're up and running
            this.setUp(true);
        }
        else
        {
            this.up = false;
            logger.error("Failed to scan host, connection to libvirt dead");
        }
    }
    
    public long computeRunningMemory()
    {
        long mem = 0;
        for (VirtGuest guest : this.getGuests())
        {
            if (guest.isRunning())
            {
                mem += guest.getMemory();
            }
        }
        return mem;
    }
    
    private void updateGuest(VirtGuest guest, LibVirtDomain domain)
    {
        LibVirtDomainDef def = domain.getDomainDef();
        guest.setCpuCount(def.getVcpu().getCount());
        guest.setMemory(def.getMemory().getBytesValue());
        guest.setCurrentMemory(def.getCurrentMemory().getBytesValue());
        guest.setDefinition(def);
        guest.setState(domain.isRunning() ? GuestState.RUNNING : GuestState.DEFINED);
        // vnc
        for (GraphicsDef gfx : def.getDevices().getGraphics())
        {
            if ("vnc".equals(gfx.getType()))
            {
                guest.setVncPort(gfx.getPort());
                guest.setVncWebsocketPort(gfx.getWebsocket());
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
    
    //

    @Override
    public int compareTo(VirtHost o)
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
        VirtHost other = (VirtHost) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }
}
