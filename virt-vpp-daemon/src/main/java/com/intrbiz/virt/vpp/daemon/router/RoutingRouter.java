package com.intrbiz.virt.vpp.daemon.router;

import com.intrbiz.metadata.Delete;
import com.intrbiz.metadata.IsaInt;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.vpp.api.model.VRFIndex;

@Prefix("/routing")
public class RoutingRouter extends VppBaseRouter
{
    
    @Post("/ipv4/vrf/:vrf")
    @JSON
    public Boolean createIPv4VRF(@IsaInt int vrf, @Param("name") String name) throws Exception
    {
        routing().createIPv4VRF(new VRFIndex(vrf), name).get();
        return true;
    }
    
    @Delete("/ipv4/vrf/:vrf")
    @JSON
    public Boolean removeIPv4VRF(@IsaInt int vrf) throws Exception
    {
        routing().removeIPv4VRF(new VRFIndex(vrf)).get();
        return true;
    }
}
