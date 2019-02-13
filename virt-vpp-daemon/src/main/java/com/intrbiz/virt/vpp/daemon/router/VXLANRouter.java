package com.intrbiz.virt.vpp.daemon.router;

import com.intrbiz.metadata.CoalesceMode;
import com.intrbiz.metadata.Delete;
import com.intrbiz.metadata.IsaInt;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.VNI;
import com.intrbiz.vpp.api.model.VRFIndex;

@Prefix("/vxlan")
public class VXLANRouter extends VppBaseRouter
{
    
    @Post("/vni/:vni")
    @JSON
    public InterfaceIndex createVXLANTunnel(
            @IsaInt int id, 
            @Param("source") String srcAddress,
            @Param("destination") String dstAddress,
            @Param("parent_interface") @IsaInt(defaultValue = -1, coalesce = CoalesceMode.ALWAYS) int parentInterface,
            @Param("vrf") @IsaInt(defaultValue = -1, coalesce = CoalesceMode.ALWAYS) int vrfIndex
    ) throws Exception
    {
        return vxlan().createVXLANTunnel(
                new VNI(id), 
                IPv4Address.fromString(srcAddress), 
                IPv4Address.fromString(dstAddress), 
                parentInterface == -1 ? null : new InterfaceIndex(parentInterface),
                vrfIndex == -1 ? null : new VRFIndex(vrfIndex)).get();
    }
    
    
    @Delete("/vni/:vni")
    @JSON
    public Boolean removeVXLANTunnel(
            @IsaInt int id, 
            @Param("source") String srcAddress,
            @Param("destination") String dstAddress,
            @Param("parent_interface") @IsaInt(defaultValue = -1, coalesce = CoalesceMode.ALWAYS) int parentInterface,
            @Param("vrf") @IsaInt(defaultValue = -1, coalesce = CoalesceMode.ALWAYS) int vrfIndex
    ) throws Exception
    {
        vxlan().removeVXLANTunnel(
            new VNI(id), 
            IPv4Address.fromString(srcAddress), 
            IPv4Address.fromString(dstAddress), 
            parentInterface == -1 ? null : new InterfaceIndex(parentInterface),
            vrfIndex == -1 ? null : new VRFIndex(vrfIndex)).get();
        return true;
    }
    
    @Post("/index/:index/bypass/on")
    @JSON
    public Boolean setIPv4VXLANBypassOn(@IsaInt int index) throws Exception
    {
        vxlan().setIPv4VXLANBypass(new InterfaceIndex(index), true).get();
        return true;
    }
    
    @Post("/index/:index/bypass/off")
    @JSON
    public Boolean setIPv4VXLANBypassOff(@IsaInt int index) throws Exception
    {
        vxlan().setIPv4VXLANBypass(new InterfaceIndex(index), false).get();
        return true;
    }
}
