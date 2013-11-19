package com.intrbiz.virt.libvirt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.Interface;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;

import com.intrbiz.data.DataAdapter;
import com.intrbiz.data.DataException;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
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
    
    public static final LibVirtAdapter sshConnect(String host, int port)
    {
        return connect("qemu+ssh://root@" + host + ":" + port + "/system");
    }
    
    public static final LibVirtAdapter sshConnect(String host)
    {
        return sshConnect(host, 22);
    }
    
    // private Logger logger = Logger.getLogger(LibvirtAdapter.class);
    
    private Connect connection;
    
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
    
    // delegates
    
    public Connect getLibVirtConnection()
    {
        return this.connection;
    }
    
    public boolean isConnected()
    {
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
        List<LibVirtDomain> domains = new LinkedList<LibVirtDomain>();
        domains.addAll(this.listRunningDomains());
        domains.addAll(this.listDefinedDomains());
        Collections.sort(domains);
        return domains;
    }

    public List<LibVirtDomain> listRunningDomains()
    {
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
        try
        {
            Domain d = this.connection.domainLookupByID(id);
            if (d != null) return new LibVirtDomain(this, d);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot lookup domain", e);
        }
        return null;
    }

    public LibVirtDomain lookupDomainByName(String name)
    {
        try
        {
            Domain d = this.connection.domainLookupByName(name);
            if (d != null) return new LibVirtDomain(this, d);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot lookup domain", e);
        }
        return null;
    }
    
    public LibVirtDomain addDomain(LibVirtDomainDef def) throws DataException
    {
        try
        {
            System.out.println("Creating VM from\n" + def);
            Domain d = this.connection.domainDefineXML(def.toString());
            return new LibVirtDomain(this, d);
        }
        catch (LibvirtException e)
        {
            throw new DataException("Could not define domain", e);
        }
    }
    
    public LibVirtNodeInfo nodeInfo()
    {
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
        try
        {
            Interface ifc = this.connection.interfaceLookupByName(name);
            return new LibVirtHostInterface(ifc.getName(), ifc.getMACString());
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot get interface info", e);
        }
    }
    
    public LibVirtHostInterface lookupHostInterfaceByMACAddress(String macAddress)
    {
        try
        {
            Interface ifc = this.connection.interfaceLookupByMACString(macAddress);
            return new LibVirtHostInterface(ifc.getName(), ifc.getMACString());
        }
        catch (LibvirtException e)
        {
            throw new DataException("Cannot get interface info", e);
        }
    }
    
    public List<LibVirtHostInterface> listHostInterfaces()
    {
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
        if (this.connection != null)
        {
            try
            {
                this.connection.close();
            }
            catch (LibvirtException e)
            {
            }
            finally
            {
                this.connection = null;
            }
        }
    }
}
