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

import com.intrbiz.data.DataAdapter;
import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.util.IdedWeakReference;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtHostInterface;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;

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

    public static class qemu
    {
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
    
    private ConcurrentMap<Integer,IdedWeakReference<LibVirtDomain>> domainsToCleanUp = new ConcurrentHashMap<Integer,IdedWeakReference<LibVirtDomain>>();
    
    private ConcurrentMap<Integer,Domain> realDomainsToCleanUp = new ConcurrentHashMap<Integer,Domain>();
    
    private AtomicInteger cleanUpId = new AtomicInteger(1);
    
    private ReferenceQueue<LibVirtDomain> cleanUpRefQueue = new ReferenceQueue<LibVirtDomain>();

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

    public Connect getLibVirtConnection()
    {
        this.checkOpen();
        return this.connection;
    }

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

    public List<LibVirtDomain> listDomains()
    {
        this.checkOpen();
        List<LibVirtDomain> domains = new LinkedList<LibVirtDomain>();
        domains.addAll(this.listRunningDomains());
        domains.addAll(this.listDefinedDomains());
        Collections.sort(domains);
        return domains;
    }

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

    public void close()
    {
        if (! this.closed)
        {
            this.closed = true;
            try
            {
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

    @SuppressWarnings("unchecked")
    protected int addDomainToCleanUp(LibVirtDomain domain)
    {
        int id = this.cleanUpId.incrementAndGet();
        this.domainsToCleanUp.put(id, new IdedWeakReference<LibVirtDomain>(id,domain, this.cleanUpRefQueue));
        this.realDomainsToCleanUp.put(id, domain.getLibVirtDomain());
        // process the reference queue
        try
        {
            while (this.cleanUpRefQueue.poll() != null)
            {
                this.domainsToCleanUp.remove(((IdedWeakReference<LibVirtDomain>) this.cleanUpRefQueue.remove()).getId());
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
}
