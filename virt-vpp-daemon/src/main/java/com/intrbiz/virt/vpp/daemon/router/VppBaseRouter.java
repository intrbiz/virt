package com.intrbiz.virt.vpp.daemon.router;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.virt.VppDaemon;
import com.intrbiz.vpp.api.VPPRecipeManager;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.module.ACL;
import com.intrbiz.vpp.api.module.Bridge;
import com.intrbiz.vpp.api.module.Graph;
import com.intrbiz.vpp.api.module.Info;
import com.intrbiz.vpp.api.module.Interfaces;
import com.intrbiz.vpp.api.module.NAT;
import com.intrbiz.vpp.api.module.Routing;
import com.intrbiz.vpp.api.module.VXLAN;

public abstract class VppBaseRouter extends Router<VppDaemon>
{
    protected VPPSimple vpp()
    {
        return this.app().getVpp();
    }
    
    protected VPPRecipeManager recipeManager()
    {
        return this.app().getRecipeManager();
    }
    
    /**
     * The system information module
     */
    protected Info info()
    {
        return vpp().info();
    }
    
    /**
     * Get the interface management module
     */
    protected Interfaces interfaces()
    {
        return vpp().interfaces();
    }
    
    /**
     * Get the VXLAN tunnel management module
     */
    protected VXLAN vxlan()
    {
        return vpp().vxlan();
    }
    
    /**
     * Get the bridge domain management module
     */
    protected Bridge bridge()
    {
        return vpp().bridge();
    }
    
    /**
     * Get the VPP processing graph management module
     */
    protected Graph graph()
    {
        return vpp().graph();
    }
    
    /**
     * Get the VPP routing management module
     */
    protected Routing routing()
    {
        return vpp().routing();
    }
    
    /**
     * Get the VPP NAT management module
     */
    protected NAT nat()
    {
        return vpp().nat();
    }
    
    /**
     * Get the VPP ACL management module
     */
    protected ACL acl()
    {
        return vpp().acl();
    }
}
