package com.intrbiz.virt.libvirt;

import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.Interface;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;

import com.intrbiz.data.DataAdapter;
import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.util.IdedWeakReference;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtHostInterface;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStoragePool;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStorageVol;

/**
 * A simple adapter to the libvirt virtualisation library.
 * 
 * Use one of the connect methods to connect to a running libvirtd and manipulate guests.
 * 
 * try (LibVirtAdapter lv = LibVirtAdapter.qemu.ssh.connect("root","localhost")
 * {
 *     List<LibVirtDomain> domains = lv.listDomains();
 * }
 * 
 */
public class LibVirtAdapter implements DataAdapter
{
    static
    {
        System.setProperty("jna.library.path", "/usr/lib/");
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
    
    // domain clean up
    
    private ConcurrentMap<Integer,IdedWeakReference<LibVirtDomain>> domainsToCleanUp = new ConcurrentHashMap<Integer,IdedWeakReference<LibVirtDomain>>();
    
    private ConcurrentMap<Integer,Domain> realDomainsToCleanUp = new ConcurrentHashMap<Integer,Domain>();
    
    private AtomicInteger cleanUpId = new AtomicInteger(1);
    
    private ReferenceQueue<LibVirtDomain> domainCleanUpRefQueue = new ReferenceQueue<LibVirtDomain>();

    // storage pool clean up
    
    private ConcurrentMap<Integer,IdedWeakReference<LibVirtStoragePool>> storagePoolsToCleanUp = new ConcurrentHashMap<Integer,IdedWeakReference<LibVirtStoragePool>>();
    
    private ConcurrentMap<Integer,StoragePool> realStoragePoolsToCleanUp = new ConcurrentHashMap<Integer,StoragePool>();
    
    private ReferenceQueue<LibVirtStoragePool> storagePoolsCleanUpRefQueue = new ReferenceQueue<LibVirtStoragePool>();
    
    // storage vol clean up
    
    private ConcurrentMap<Integer,IdedWeakReference<LibVirtStorageVol>> storageVolsToCleanUp = new ConcurrentHashMap<Integer,IdedWeakReference<LibVirtStorageVol>>();
    
    private ConcurrentMap<Integer,StorageVol> realStorageVolsToCleanUp = new ConcurrentHashMap<Integer,StorageVol>();
    
    private ReferenceQueue<LibVirtStorageVol> storageVolsCleanUpRefQueue = new ReferenceQueue<LibVirtStorageVol>();
    
    protected LibVirtAdapter(String url) throws DataException
    {
        super();
        try
        {
            this.connection = new Connect(url);
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
            System.out.println("Creating VM from\n" + def);
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
            throw new DataException("Cannot lookup storage vol", e);
        }
    }

    /**
     * Close this connection
     */
    public void close()
    {
        if (! this.closed)
        {
            this.closed = true;
            try
            {
                // clean up domains
                for (Entry<Integer, Domain> e : this.realDomainsToCleanUp.entrySet())
                {
                    Domain dom = e.getValue();
                    if (dom != null)
                    {
                        try
                        {
                            dom.free();
                        }
                        catch (LibvirtException ex)
                        {
                        }
                    }
                }
                this.domainsToCleanUp.clear();
                this.realDomainsToCleanUp.clear();
                // clean up storage pools
                for (Entry<Integer, StoragePool> e : this.realStoragePoolsToCleanUp.entrySet())
                {
                    StoragePool pool = e.getValue();
                    if (pool != null)
                    {
                        try
                        {
                            pool.free();
                        }
                        catch (LibvirtException ex)
                        {
                        }
                    }
                }
                this.storagePoolsToCleanUp.clear();
                this.realStoragePoolsToCleanUp.clear();
                // clean up storage vols
                for (Entry<Integer, StorageVol> e : this.realStorageVolsToCleanUp.entrySet())
                {
                    StorageVol vol = e.getValue();
                    if (vol != null)
                    {
                        try
                        {
                            vol.free();
                        }
                        catch (LibvirtException ex)
                        {
                        }
                    }
                }
                this.storageVolsToCleanUp.clear();
                this.realStorageVolsToCleanUp.clear();
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

    protected LibVirtDomain newLibVirtDomain(Domain domain)
    {
        return new LibVirtDomain(this, domain) {
            
            private int cleanUpId;
            
            @Override
            protected void addDomainToCleanUp()
            {
                this.cleanUpId = LibVirtAdapter.this.addDomainToCleanUp(this);
            }

            @Override
            protected void removeDomainFromCleanUp()
            {
                LibVirtAdapter.this.removeDomainFromCleanUp(this.cleanUpId);
            }
        };
    }
    
    protected LibVirtStoragePool newLibVirtStoragePool(StoragePool pool)
    {
        return new LibVirtStoragePool(this, pool) {
            
            private int cleanUpId;
            
            @Override
            protected void addStoragePoolToCleanUp()
            {
                this.cleanUpId = LibVirtAdapter.this.addStoragePoolToCleanUp(this);
            }

            @Override
            protected void removeStoragePoolFromCleanUp()
            {
                LibVirtAdapter.this.removeStoragePoolFromCleanUp(this.cleanUpId);
            }

            @Override
            protected LibVirtStorageVol newLibVirtStorageVol(StorageVol vol)
            {
                return LibVirtAdapter.this.newLibVirtStorageVol(vol);
            }
        };
    }
    
    protected LibVirtStorageVol newLibVirtStorageVol(StorageVol vol)
    {
        return new LibVirtStorageVol(this, vol) {
            
            private int cleanUpId;
            
            @Override
            protected void addStorageVolToCleanUp()
            {
                this.cleanUpId = LibVirtAdapter.this.addStorageVolToCleanUp(this);
            }

            @Override
            protected void removeStorageVolFromCleanUp()
            {
                LibVirtAdapter.this.removeStorageVolFromCleanUp(this.cleanUpId);
            }

            @Override
            protected LibVirtStoragePool newStoragePool(StoragePool pool)
            {
                return LibVirtAdapter.this.newLibVirtStoragePool(pool);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected int addDomainToCleanUp(LibVirtDomain domain)
    {
        int id = this.cleanUpId.incrementAndGet();
        this.domainsToCleanUp.put(id, new IdedWeakReference<LibVirtDomain>(id,domain, this.domainCleanUpRefQueue));
        this.realDomainsToCleanUp.put(id, domain.getLibVirtDomain());
        // process the reference queue
        try
        {
            while (this.domainCleanUpRefQueue.poll() != null)
            {
                this.domainsToCleanUp.remove(((IdedWeakReference<LibVirtDomain>) this.domainCleanUpRefQueue.remove()).getId());
            }
        }
        catch (InterruptedException e)
        {
        }
        return id;
    }
    
    protected void removeDomainFromCleanUp(int id)
    {
        this.realDomainsToCleanUp.remove(id);
    }
    
    @SuppressWarnings("unchecked")
    protected int addStoragePoolToCleanUp(LibVirtStoragePool pool)
    {
        int id = this.cleanUpId.incrementAndGet();
        this.storagePoolsToCleanUp.put(id, new IdedWeakReference<LibVirtStoragePool>(id,pool, this.storagePoolsCleanUpRefQueue));
        this.realStoragePoolsToCleanUp.put(id, pool.getLibVirtStoragePool());
        // process the reference queue
        try
        {
            while (this.storagePoolsCleanUpRefQueue.poll() != null)
            {
                this.storagePoolsToCleanUp.remove(((IdedWeakReference<LibVirtStoragePool>) this.storagePoolsCleanUpRefQueue.remove()).getId());
            }
        }
        catch (InterruptedException e)
        {
        }
        return id;
    }
    
    protected void removeStoragePoolFromCleanUp(int id)
    {
        this.realStoragePoolsToCleanUp.remove(id);
    }
    
    @SuppressWarnings("unchecked")
    protected int addStorageVolToCleanUp(LibVirtStorageVol vol)
    {
        int id = this.cleanUpId.incrementAndGet();
        this.storageVolsToCleanUp.put(id, new IdedWeakReference<LibVirtStorageVol>(id, vol, this.storageVolsCleanUpRefQueue));
        this.realStorageVolsToCleanUp.put(id, vol.getLibVirtStorageVol());
        // process the reference queue
        try
        {
            while (this.storageVolsCleanUpRefQueue.poll() != null)
            {
                this.storageVolsToCleanUp.remove(((IdedWeakReference<LibVirtStorageVol>) this.storageVolsCleanUpRefQueue.remove()).getId());
            }
        }
        catch (InterruptedException e)
        {
        }
        return id;
    }
    
    protected void removeStorageVolFromCleanUp(int id)
    {
        this.realStorageVolsToCleanUp.remove(id);
    }
}
