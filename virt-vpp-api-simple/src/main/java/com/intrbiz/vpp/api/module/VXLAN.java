package com.intrbiz.vpp.api.module;

import java.util.concurrent.Future;

import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.VRFIndex;
import com.intrbiz.vpp.api.model.VNI;

public interface VXLAN
{
    Future<InterfaceIndex> createVXLANTunnel(VNI id, IPv4Address srcAddress, IPv4Address dstAddress, InterfaceIndex parentInterface, VRFIndex vrfIndex);
    
    default Future<InterfaceIndex> createVXLANTunnel(VNI id, IPv4Address srcAddress, IPv4Address dstAddress)
    {
        return this.createVXLANTunnel(id, srcAddress, dstAddress, null, null);
    }
    
    Future<Void> removeVXLANTunnel(VNI id, IPv4Address srcAddress, IPv4Address dstAddress, InterfaceIndex parentInterface, VRFIndex vrfIndex);
    
    default Future<Void> removeVXLANTunnel(VNI id, IPv4Address srcAddress, IPv4Address dstAddress)
    {
        return this.removeVXLANTunnel(id, srcAddress, dstAddress, null, null);
    }
    
    Future<Void> setIPv4VXLANBypass(InterfaceIndex index, boolean enabled);
}
