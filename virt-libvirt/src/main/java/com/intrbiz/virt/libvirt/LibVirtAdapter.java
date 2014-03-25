package com.intrbiz.virt.libvirt;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.Error.ErrorNumber;
import org.libvirt.Interface;
import org.libvirt.Library;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;

import com.intrbiz.data.DataAdapter;
import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.event.LibVirtDomainLifecycleEventHandler;
import com.intrbiz.virt.libvirt.event.LibVirtDomainRebootEventHandler;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.util.IdedWeakReference;
import com.intrbiz.virt.libvirt.model.util.LibVirtCleanupWrapper;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtHostInterface;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStoragePool;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStorageVol;

/**
 * <p>
 * A simple adapter to the libvirt virtualisation library.
 * </p>
 * <p>
 * Use one of the connect methods to connect to a running libvirtd and manipulate guests.
 * </p>
 * <code><pre>
 * try (LibVirtAdapter lv = LibVirtAdapter.qemu.ssh.connect("root","localhost") 
 * { 
 *     List<LibVirtDomain> domains = lv.listDomains(); 
 * }
 * </pre></code>
 * 
 */
public class LibVirtAdapter implements DataAdapter
{
    static
    {
        // setup libvirt
        System.setProperty("jna.library.path", "/usr/lib");
        try
        {
            // init the event loop
            try
            {
                Library.initEventLoop();
            }
            catch (LibvirtException e)
            {
                throw new RuntimeException("Failed to initialise the libvirt event loop!", e);
            }
            // launch the event loop
            final Logger logger = Logger.getLogger(LibVirtAdapter.class);
            new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Library.runEventLoop();
                    }
                    catch (LibvirtException e)
                    {
                        logger.error("Exception thrown whilst processing the libvirt event loop", e);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }, "LibVirt Eventloop").start();
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Failed to initialise libvirt", e);
        }
    }

    public static final LibVirtAdapter connect(String url)
    {
        return new LibVirtAdapter(url);
    }

    public static final LibVirtAdapter connect(String driver, String host)
    {
        return connect(driver + "://" + host + "/system");
    }

    public static final LibVirtAdapter connect(String driver, String host, int port)
    {
        return connect(driver + "://" + host + ":" + port + "/system");
    }

    public static final LibVirtAdapter connect(String driver, String transport, String host, int port)
    {
        return connect(driver + "+" + transport + "://" + host + ":" + port + "/system");
    }

    public static final LibVirtAdapter connect(String driver, String transport, String user, String host, int port)
    {
        return connect(driver + "+" + transport + "://" + user + "@" + host + ":" + port + "/system");
    }

    public static final LibVirtAdapter connect(String driver, String transport, String host)
    {
        return connect(driver + "+" + transport + "://" + host + "/system");
    }

    public static final LibVirtAdapter connect(String driver, String transport, String user, String host)
    {
        return connect(driver + "+" + transport + "://" + user + "@" + host + "/system");
    }

    /**
     * Connect to the qemu driver
     */
    public static class qemu
    {
        /**
         * Connect over SSH
         */
        public static class ssh
        {
            public static final LibVirtAdapter connect(String host, int port)
            {
                return LibVirtAdapter.connect("qemu", "ssh", host, port);
            }

            public static final LibVirtAdapter connect(String host)
            {
                return LibVirtAdapter.connect("qemu", "ssh", host);
            }

            public static final LibVirtAdapter connect(String user, String host, int port)
            {
                return LibVirtAdapter.connect("qemu", "ssh", user, host, port);
            }

            public static final LibVirtAdapter connect(String user, String host)
            {
                return LibVirtAdapter.connect("qemu", "ssh", user, host);
            }
        }

        /**
         * Connect over TCP
         */
        public static class tcp
        {
            public static final LibVirtAdapter connect(String host, int port)
            {
                return LibVirtAdapter.connect("qemu", "tcp", host, port);
            }

            public static final LibVirtAdapter connect(String host)
            {
                return LibVirtAdapter.connect("qemu", "tcp", host);
            }
        }
    }

    // private Logger logger = Logger.getLogger(LibvirtAdapter.class);

    private Connect connection;

    private volatile boolean closed = false;

    // clean up

    private AtomicInteger cleanUpId = new AtomicInteger();

    private ConcurrentMap<Integer, IdedWeakReference<Object>> underlyingObjectsToCleanUp = new ConcurrentHashMap<Integer, IdedWeakReference<Object>>();

    private ReferenceQueue<Object> cleanUpRefQueue = new ReferenceQueue<Object>();

    private ConcurrentMap<Integer, LibVirtCleanupWrapper> wrappersToCleanUp = new ConcurrentHashMap<Integer, LibVirtCleanupWrapper>();

    //

    protected LibVirtAdapter(String url) throws DataException
    {
        super();
        try
        {
            this.connection = new Connect(url);
            this.connection.setKeepAlive(5, 3);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to connect to libvirtd: " + url);
        }
    }

    public String getName()
    {
        return "libvirt";
    }

    // check

    public void checkOpen()
    {
        if (this.closed) throw new DataException("LibVirtAdapter is closed, are you trying to use it after calling close?");
    }

    // delegates

    /**
     * Get the underlying libvirt connection
     */
    public Connect getLibVirtConnection()
    {
        this.checkOpen();
        return this.connection;
    }

    /**
     * Is this adapter still connected
     */
    public boolean isConnected()
    {
        this.checkOpen();
        try
        {
            return this.connection != null && this.connection.isConnected();
        }
        catch (LibvirtException e)
        {
        }
        return false;
    }

    /**
     * Get the connection URL
     */
    public String getURL()
    {
        this.checkOpen();
        try
        {
            return this.connection.getURI();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    /**
     * Get the connection type
     */
    public String getType()
    {
        this.checkOpen();
        try
        {
            return this.connection.getType();
        }
        catch (LibvirtException e)
        {
            throw new DataException(e);
        }
    }

    /**
     * List all domains on the host, running or just defined
     */
    public List<LibVirtDomain> listDomains()
    {
        this.checkOpen();
        List<LibVirtDomain> domains = new LinkedList<LibVirtDomain>();
        domains.addAll(this.listRunningDomains());
        domains.addAll(this.listDefinedDomains());
        Collections.sort(domains);
        return domains;
    }

    /**
     * List only domains which are running on the host
     */
    public List<LibVirtDomain> listRunningDomains()
    {
        this.checkOpen();
        List<LibVirtDomain> domains = new LinkedList<LibVirtDomain>();
        try
        {
            for (int id : this.connection.listDomains())
            {
                domains.add(this.lookupDomainById(id));
            }
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot list running domains", e);
        }
        Collections.sort(domains);
        return domains;
    }

    /**
     * List only domains which are defined on the host
     */
    public List<LibVirtDomain> listDefinedDomains()
    {
        this.checkOpen();
        List<LibVirtDomain> domains = new LinkedList<LibVirtDomain>();
        try
        {
            for (String name : this.connection.listDefinedDomains())
            {
                domains.add(this.lookupDomainByName(name));
            }
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot list defined domains", e);
        }
        Collections.sort(domains);
        return domains;
    }

    /**
     * Lookup a domain by its running id
     */
    public LibVirtDomain lookupDomainById(int id)
    {
        this.checkOpen();
        try
        {
            Domain d = this.connection.domainLookupByID(id);
            if (d != null) return newLibVirtDomain(d);
        }
        catch (LibvirtException e)
        {
            // rather than throw return null
            if (ErrorNumber.VIR_ERR_NO_DOMAIN == e.getError().getCode()) return null;
            throw new DataException("Cannot lookup domain", e);
        }
        return null;
    }

    /**
     * Lookup a domain by it name
     */
    public LibVirtDomain lookupDomainByName(String name)
    {
        this.checkOpen();
        try
        {
            Domain d = this.connection.domainLookupByName(name);
            if (d != null) return newLibVirtDomain(d);
        }
        catch (LibvirtException e)
        {
            // rather than throw return null
            if (ErrorNumber.VIR_ERR_NO_DOMAIN == e.getError().getCode()) return null;
            throw new DataException("Cannot lookup domain", e);
        }
        return null;
    }

    /**
     * Lookup a domain by its UUID
     */
    public LibVirtDomain lookupDomainByUuid(UUID uuid)
    {
        this.checkOpen();
        try
        {
            Domain d = this.connection.domainLookupByUUID(uuid);
            if (d != null) return newLibVirtDomain(d);
        }
        catch (LibvirtException e)
        {
            // rather than throw return null
            if (ErrorNumber.VIR_ERR_NO_DOMAIN == e.getError().getCode()) return null;
            throw new DataException("Cannot lookup domain", e);
        }
        return null;
    }

    /**
     * Define a domain with the given definition
     */
    public LibVirtDomain addDomain(LibVirtDomainDef def) throws DataException
    {
        this.checkOpen();
        try
        {
            Domain d = this.connection.domainDefineXML(def.toString());
            return newLibVirtDomain(d);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Could not define domain", e);
        }
    }

    /**
     * Get information about the host hardware
     */
    public LibVirtNodeInfo nodeInfo()
    {
        this.checkOpen();
        try
        {
            NodeInfo ni = this.connection.nodeInfo();
            return new LibVirtNodeInfo(ni.model, ni.memory * 1024L, ni.cpus, ni.mhz, ni.nodes, ni.sockets, ni.cores, ni.threads);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot get node info", e);
        }
    }

    /**
     * Lookup a host interface by name
     */
    public LibVirtHostInterface lookupHostInterfaceByName(String name)
    {
        this.checkOpen();
        try
        {
            Interface ifc = this.connection.interfaceLookupByName(name);
            try
            {
                return new LibVirtHostInterface(ifc.getName(), ifc.getMACString());
            }
            finally
            {
                if (ifc != null) ifc.free();
            }
        }
        catch (LibvirtException e)
        {
            // rather than throw return null
            if (ErrorNumber.VIR_ERR_NO_INTERFACE == e.getError().getCode()) return null;
            throw new DataException("Cannot get interface info", e);
        }
    }

    /**
     * Lookup a host interface by MAC address
     */
    public LibVirtHostInterface lookupHostInterfaceByMACAddress(String macAddress)
    {
        this.checkOpen();
        try
        {
            Interface ifc = this.connection.interfaceLookupByMACString(macAddress);
            try
            {
                return new LibVirtHostInterface(ifc.getName(), ifc.getMACString());
            }
            finally
            {
                if (ifc != null) ifc.free();
            }
        }
        catch (LibvirtException e)
        {
            // rather than throw return null
            if (ErrorNumber.VIR_ERR_NO_INTERFACE == e.getError().getCode()) return null;
            throw new DataException("Cannot get interface info", e);
        }
    }

    /**
     * List the host interfaces on this host
     */
    public List<LibVirtHostInterface> listHostInterfaces()
    {
        this.checkOpen();
        List<LibVirtHostInterface> interfaces = new LinkedList<LibVirtHostInterface>();
        try
        {
            for (String name : this.connection.listInterfaces())
            {
                interfaces.add(this.lookupHostInterfaceByName(name));
            }
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot get interfaces", e);
        }
        Collections.sort(interfaces);
        return interfaces;
    }

    public LibVirtStoragePool lookupStoragePoolByName(String name)
    {
        this.checkOpen();
        try
        {
            StoragePool pool = this.connection.storagePoolLookupByName(name);
            return this.newLibVirtStoragePool(pool);
        }
        catch (LibvirtException e)
        {
            // rather than throw on no pool we will return null;
            if (ErrorNumber.VIR_ERR_NO_STORAGE_POOL == e.getError().getCode()) return null;
            throw new DataException("Cannot lookup storage pool", e);
        }
    }

    public LibVirtStoragePool lookupStoragePoolByUuid(UUID id)
    {
        this.checkOpen();
        try
        {
            StoragePool pool = this.connection.storagePoolLookupByUUID(id);
            return this.newLibVirtStoragePool(pool);
        }
        catch (LibvirtException e)
        {
            // rather than throw on no pool we will return null;
            if (ErrorNumber.VIR_ERR_NO_STORAGE_POOL == e.getError().getCode()) return null;
            throw new DataException("Cannot lookup storage pool", e);
        }
    }

    public List<LibVirtStoragePool> listStoragePools()
    {
        this.checkOpen();
        List<LibVirtStoragePool> l = new LinkedList<LibVirtStoragePool>();
        try
        {
            for (String name : this.connection.listStoragePools())
            {
                l.add(this.lookupStoragePoolByName(name));
            }
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot lookup storage pool", e);
        }
        Collections.sort(l);
        return l;
    }

    public LibVirtStorageVol lookupStorageVolByPath(String path)
    {
        this.checkOpen();
        try
        {
            StorageVol vol = this.connection.storageVolLookupByPath(path);
            return this.newLibVirtStorageVol(vol);
        }
        catch (LibvirtException e)
        {
            // rather than throw on no volume we will return null;
            if (ErrorNumber.VIR_ERR_NO_STORAGE_VOL == e.getError().getCode()) return null;
            throw new DataException("Cannot lookup storage vol", e);
        }
    }

    public LibVirtStorageVol lookupStorageVolByKey(String key)
    {
        this.checkOpen();
        try
        {
            StorageVol vol = this.connection.storageVolLookupByKey(key);
            return this.newLibVirtStorageVol(vol);
        }
        catch (LibvirtException e)
        {
            // rather than throw on no volume we will return null;
            if (ErrorNumber.VIR_ERR_NO_STORAGE_VOL == e.getError().getCode()) return null;
            throw new DataException("Cannot lookup storage vol", e);
        }
    }

    public LibVirtDomainLifecycleEventHandler registerLifecycleEventHandler(LibVirtDomainLifecycleEventHandler handler)
    {
        return this.registerEventHandler(handler);
    }

    public LibVirtDomainRebootEventHandler registerRebootEventHandler(LibVirtDomainRebootEventHandler handler)
    {
        return this.registerEventHandler(handler);
    }

    public <T extends LibVirtEventHandler<?>> T registerEventHandler(T handler)
    {
        return this.registerEventHandler(null, handler);
    }

    public <T extends LibVirtEventHandler<?>> T registerEventHandler(LibVirtDomain domain, T handler)
    {
        try
        {
            handler.register(this, domain);
            return handler;
        }
        catch (LibvirtException e)
        {
            throw new DataException("Failed to register for event", e);
        }
    }

    /**
     * Close this connection
     */
    public void close()
    {
        if (!this.closed)
        {
            this.closed = true;
            try
            {
                // clean up objects
                for (LibVirtCleanupWrapper e : this.wrappersToCleanUp.values())
                {
                    e.free();
                }
                this.underlyingObjectsToCleanUp.clear();
                this.wrappersToCleanUp.clear();
            }
            finally
            {
                try
                {
                    this.connection.close();
                }
                catch (LibvirtException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    this.connection = null;
                }
            }
        }
    }

    LibVirtDomain newLibVirtDomain(Domain domain)
    {
        return new LibVirtDomain(this, domain)
        {

            private int cleanUpId;

            @Override
            protected void addDomainToCleanUp()
            {
                this.cleanUpId = LibVirtAdapter.this.addObjectToCleanUp(this.domain, this.cleanup);
            }

            @Override
            protected void removeDomainFromCleanUp()
            {
                LibVirtAdapter.this.removeObjectFromCleanUp(this.cleanUpId);
            }
        };
    }

    LibVirtStoragePool newLibVirtStoragePool(StoragePool pool)
    {
        return new LibVirtStoragePool(this, pool)
        {

            private int cleanUpId;

            @Override
            protected void addStoragePoolToCleanUp()
            {
                this.cleanUpId = LibVirtAdapter.this.addObjectToCleanUp(this.pool, this.cleanup);
            }

            @Override
            protected void removeStoragePoolFromCleanUp()
            {
                LibVirtAdapter.this.removeObjectFromCleanUp(this.cleanUpId);
            }

            @Override
            protected LibVirtStorageVol newLibVirtStorageVol(StorageVol vol)
            {
                return LibVirtAdapter.this.newLibVirtStorageVol(vol);
            }
        };
    }

    LibVirtStorageVol newLibVirtStorageVol(StorageVol vol)
    {
        return new LibVirtStorageVol(this, vol)
        {

            private int cleanUpId;

            @Override
            protected void addStorageVolToCleanUp()
            {
                this.cleanUpId = LibVirtAdapter.this.addObjectToCleanUp(this.vol, this.cleanup);
            }

            @Override
            protected void removeStorageVolFromCleanUp()
            {
                LibVirtAdapter.this.removeObjectFromCleanUp(this.cleanUpId);
            }

            @Override
            protected LibVirtStoragePool newStoragePool(StoragePool pool)
            {
                return LibVirtAdapter.this.newLibVirtStoragePool(pool);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected int addObjectToCleanUp(Object underlying, LibVirtCleanupWrapper cleanup)
    {
        int id = this.cleanUpId.incrementAndGet();
        this.underlyingObjectsToCleanUp.put(id, new IdedWeakReference<Object>(id, underlying, this.cleanUpRefQueue));
        this.wrappersToCleanUp.put(id, cleanup);
        // process the reference queue
        Reference<? extends Object> ref;
        while ((ref = this.cleanUpRefQueue.poll()) != null)
        {
            this.wrappersToCleanUp.remove(((IdedWeakReference<Object>) ref).getId());
        }
        return id;
    }

    protected void removeObjectFromCleanUp(int id)
    {
        this.wrappersToCleanUp.remove(id);
    }
}
