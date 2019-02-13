package com.intrbiz.vpp.module;

import java.util.concurrent.Future;

import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.VRFIndex;
import com.intrbiz.vpp.api.model.VNI;
import com.intrbiz.vpp.api.module.VXLAN;
import com.intrbiz.vpp.core.FutureMapping;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;
import com.intrbiz.vpp.util.JVPPUtil;

import io.fd.vpp.jvpp.core.dto.SwInterfaceSetVxlanBypass;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetVxlanBypassReply;
import io.fd.vpp.jvpp.core.dto.VxlanAddDelTunnel;
import io.fd.vpp.jvpp.core.dto.VxlanAddDelTunnelReply;

public class VXLANImpl extends VPPModuleImpl implements VXLAN
{
    public VXLANImpl(VPPSession session)
    {
        super(session);
    }

    @Override
    public Future<InterfaceIndex> createVXLANTunnel(VNI id, IPv4Address srcAddress, IPv4Address dstAddress, InterfaceIndex parentInterface, VRFIndex vrfIndex)
    {
        VxlanAddDelTunnel request = new VxlanAddDelTunnel();
        request.isAdd = JVPPUtil.TRUE;
        request.isIpv6 = JVPPUtil.FALSE;
        request.vni = id.getValue();
        request.instance = 0xFFFFFFFF;
        request.srcAddress = srcAddress.getValue();
        request.dstAddress = dstAddress.getValue();
        request.decapNextIndex = 0xFFFFFFFF;
        if (parentInterface != null) request.mcastSwIfIndex = parentInterface.getValue();
        if (vrfIndex != null) request.encapVrfId = vrfIndex.getValue();
        return new FutureMapping<VxlanAddDelTunnelReply, InterfaceIndex>(
                this.session().core().vxlanAddDelTunnel(request).toCompletableFuture(),
                (reply) -> new InterfaceIndex(reply.swIfIndex)
        );
    }
    
    @Override
    public Future<Void> removeVXLANTunnel(VNI id, IPv4Address srcAddress, IPv4Address dstAddress, InterfaceIndex parentInterface, VRFIndex vrfIndex)
    {
        VxlanAddDelTunnel request = new VxlanAddDelTunnel();
        request.isAdd = JVPPUtil.FALSE;
        request.isIpv6 = JVPPUtil.FALSE;
        request.vni = id.getValue();
        request.instance = 0xFFFFFFFF;
        request.srcAddress = srcAddress.getValue();
        request.dstAddress = dstAddress.getValue();
        request.decapNextIndex = 0xFFFFFFFF;
        if (parentInterface != null) request.mcastSwIfIndex = parentInterface.getValue();
        if (vrfIndex != null) request.encapVrfId = vrfIndex.getValue();
        return new FutureMapping<VxlanAddDelTunnelReply, Void>(
                this.session().core().vxlanAddDelTunnel(request).toCompletableFuture(),
                (reply) -> null
        );
    }

    @Override
    public Future<Void> setIPv4VXLANBypass(InterfaceIndex index, boolean enabled)
    {
        SwInterfaceSetVxlanBypass req = new SwInterfaceSetVxlanBypass();
        req.swIfIndex = index.getValue();
        req.enable = JVPPUtil.convertBoolean(enabled);
        req.isIpv6 = JVPPUtil.FALSE;
        return new FutureMapping<SwInterfaceSetVxlanBypassReply, Void>(
                this.session().core().swInterfaceSetVxlanBypass(req).toCompletableFuture(),
                (reply) -> null
        );
    }
}
