package com.intrbiz.vpp.api;

import java.io.IOException;

import com.intrbiz.vpp.api.module.ACL;
import com.intrbiz.vpp.api.module.Bridge;
import com.intrbiz.vpp.api.module.Graph;
import com.intrbiz.vpp.api.module.Info;
import com.intrbiz.vpp.api.module.Interfaces;
import com.intrbiz.vpp.api.module.NAT;
import com.intrbiz.vpp.api.module.Routing;
import com.intrbiz.vpp.api.module.VXLAN;
import com.intrbiz.vpp.core.VPPSession;

public interface VPPSimple extends AutoCloseable
{
    /**
     * Get the client name for this session
     */
    String getClientName();
    
    /**
     * The system information module
     */
    Info info();
    
    /**
     * Get the interface management module
     */
    Interfaces interfaces();
    
    /**
     * Get the VXLAN tunnel management module
     */
    VXLAN vxlan();
    
    /**
     * Get the bridge domain management module
     */
    Bridge bridge();
    
    /**
     * Get the VPP processing graph management module
     */
    Graph graph();
    
    /**
     * Get the VPP routing management module
     */
    Routing routing();
    
    /**
     * Get the VPP NAT management module
     */
    NAT nat();
    
    /**
     * Get the VPP ACL management module
     */
    ACL acl();
    
    /**
     * Close this session to the VPP daemon
     */
    void close();

    /**
     * Connect to the locally running VPP daemon
     */
    public static VPPSimple connect(String clientName) throws IOException
    {
        return new VPPSession(clientName);
    }
}
