package com.intrbiz.vpp.core;

import java.io.IOException;

import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.module.ACL;
import com.intrbiz.vpp.api.module.Bridge;
import com.intrbiz.vpp.api.module.Graph;
import com.intrbiz.vpp.api.module.Info;
import com.intrbiz.vpp.api.module.Interfaces;
import com.intrbiz.vpp.api.module.NAT;
import com.intrbiz.vpp.api.module.Routing;
import com.intrbiz.vpp.api.module.VXLAN;
import com.intrbiz.vpp.module.ACLImpl;
import com.intrbiz.vpp.module.BridgeDomainImpl;
import com.intrbiz.vpp.module.GraphImpl;
import com.intrbiz.vpp.module.InfoImpl;
import com.intrbiz.vpp.module.InterfacesImpl;
import com.intrbiz.vpp.module.NATImpl;
import com.intrbiz.vpp.module.RoutingImpl;
import com.intrbiz.vpp.module.VXLANImpl;

import io.fd.vpp.jvpp.JVppRegistry;
import io.fd.vpp.jvpp.JVppRegistryImpl;
import io.fd.vpp.jvpp.core.JVppCoreImpl;
import io.fd.vpp.jvpp.core.future.FutureJVppCore;
import io.fd.vpp.jvpp.core.future.FutureJVppCoreFacade;

/**
 * A session to the VPP daemon, this covers the low level connection to VPP and associated plugins
 */
public class VPPSession implements VPPSimple
{
    private final String clientName;
    
    // JVPP
    
    private final JVppRegistry jvppRegistry;
    
    // JVPP Plugins
    
    private FutureJVppCore core;
    
    // modules
    
    private Info info;
    
    private Interfaces interfaces;
    
    private VXLAN vxlan;
    
    private Bridge brdigeDomain;
    
    private GraphImpl graph;
    
    private RoutingImpl routing;
    
    private NATImpl nat;
    
    private ACLImpl acl;
    
    public VPPSession(String clientName) throws IOException
    {
        super();
        this.clientName = clientName;
        this.jvppRegistry = new JVppRegistryImpl(this.clientName);
    }
    
    @Override
    public String getClientName()
    {
        return this.clientName;
    }
    
    /**
     * Get the low level Jvpp registry.
     */
    public JVppRegistry jvppRegistry()
    {
        return this.jvppRegistry;
    }
    
    /**
     * Get the low level Jvpp Core plugin
     */
    public FutureJVppCore core()
    {
        if (this.core == null)
        {
            try
            {
                this.core = new FutureJVppCoreFacade(this.jvppRegistry, new JVppCoreImpl());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return this.core;
    }
    
    public void close()
    {
        safeClose(this.core);
        safeClose(this.jvppRegistry);
    }
    
    private static void safeClose(AutoCloseable thing)
    {
        if (thing != null)
        {
            try
            {
                thing.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    // modules
    
    @Override
    public Info info()
    {
        if (this.info == null)
            this.info = new InfoImpl(this);
        return this.info;
    }
    
    @Override
    public Interfaces interfaces()
    {
        if (this.interfaces == null)
            this.interfaces = new InterfacesImpl(this);
        return this.interfaces;
    }
    
    @Override
    public VXLAN vxlan()
    {
        if (this.vxlan == null)
            this.vxlan = new VXLANImpl(this);
        return this.vxlan;
    }
    
    @Override
    public Bridge bridge()
    {
        if (this.brdigeDomain == null)
            this.brdigeDomain = new BridgeDomainImpl(this);
        return this.brdigeDomain;
    }
    
    @Override
    public Graph graph()
    {
        if (this.graph == null)
            this.graph = new GraphImpl(this);
        return this.graph;
    }
    
    @Override
    public Routing routing()
    {
        if (this.routing == null)
            this.routing = new RoutingImpl(this);
        return this.routing;
    }
    
    @Override
    public NAT nat()
    {
        if (this.nat == null)
            this.nat = new NATImpl(this);
        return this.nat;
    }
    
    @Override
    public ACL acl()
    {
        if (this.acl == null)
            this.acl = new ACLImpl(this);
        return this.acl;
    }
}
