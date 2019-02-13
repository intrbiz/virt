package com.intrbiz.vpp.module;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.model.VRFIndex;
import com.intrbiz.vpp.api.model.VhostUserMode;
import com.intrbiz.vpp.api.module.Interfaces;
import com.intrbiz.vpp.core.FutureMapping;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;
import com.intrbiz.vpp.util.JVPPUtil;

import io.fd.vpp.jvpp.core.dto.AfPacketCreate;
import io.fd.vpp.jvpp.core.dto.AfPacketCreateReply;
import io.fd.vpp.jvpp.core.dto.AfPacketDelete;
import io.fd.vpp.jvpp.core.dto.AfPacketDeleteReply;
import io.fd.vpp.jvpp.core.dto.CreateLoopback;
import io.fd.vpp.jvpp.core.dto.CreateLoopbackReply;
import io.fd.vpp.jvpp.core.dto.CreateVhostUserIf;
import io.fd.vpp.jvpp.core.dto.CreateVhostUserIfReply;
import io.fd.vpp.jvpp.core.dto.DeleteLoopback;
import io.fd.vpp.jvpp.core.dto.DeleteLoopbackReply;
import io.fd.vpp.jvpp.core.dto.DeleteVhostUserIf;
import io.fd.vpp.jvpp.core.dto.DeleteVhostUserIfReply;
import io.fd.vpp.jvpp.core.dto.SwInterfaceAddDelAddress;
import io.fd.vpp.jvpp.core.dto.SwInterfaceAddDelAddressReply;
import io.fd.vpp.jvpp.core.dto.SwInterfaceDetails;
import io.fd.vpp.jvpp.core.dto.SwInterfaceDetailsReplyDump;
import io.fd.vpp.jvpp.core.dto.SwInterfaceDump;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetFlags;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetFlagsReply;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetMacAddress;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetMacAddressReply;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetMtu;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetMtuReply;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetTable;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetTableReply;
import io.fd.vpp.jvpp.core.dto.SwInterfaceTagAddDel;
import io.fd.vpp.jvpp.core.dto.SwInterfaceTagAddDelReply;

public class InterfacesImpl extends VPPModuleImpl implements Interfaces
{
    public InterfacesImpl(VPPSession session)
    {
        super(session);
    }

    @Override
    public Future<List<InterfaceDetail>> listInterfaces()
    {
        return new FutureMapping<SwInterfaceDetailsReplyDump, List<InterfaceDetail>>(
                this.session().core().swInterfaceDump(new SwInterfaceDump()).toCompletableFuture(), 
                (reply) -> reply.swInterfaceDetails.stream().map(InterfacesImpl::interfaceDetail).collect(Collectors.toList())
        );
    }
    
    @Override
    public Future<InterfaceDetail> getInterface(InterfaceIndex index)
    {
        return new FutureMapping<List<InterfaceDetail>, InterfaceDetail>(
                this.listInterfaces(),
                (interfaces) -> interfaces.stream()
                    .filter((i) -> index.equals(i.getIndex())).findFirst().orElse(null)
        );
    }
    
    @Override
    public Future<InterfaceDetail> getInterface(String name)
    {
        return new FutureMapping<List<InterfaceDetail>, InterfaceDetail>(
                this.listInterfaces(),
                (interfaces) -> interfaces.stream()
                    .filter((i) -> name.equals(i.getName())).findFirst().orElse(null)
        );
    }

    @Override
    public Future<InterfaceIndex> createHostInterface(String hostInterfaceName, MACAddress mac)
    {
        AfPacketCreate create = new AfPacketCreate();
        create.hostIfName = JVPPUtil.convertString(Objects.requireNonNull(hostInterfaceName));
        create.useRandomHwAddr = (byte) (mac == null ? 1 : 0);
        if (mac != null) create.hwAddr = mac.getValue();
        return new FutureMapping<AfPacketCreateReply, InterfaceIndex>(
                this.session().core().afPacketCreate(create).toCompletableFuture(),
                (reply) -> new InterfaceIndex(reply.swIfIndex)
        );
    }
    
    @Override
    public Future<Void> removeHostInterface(String hostInterfaceName)
    {
        AfPacketDelete delete = new AfPacketDelete();
        delete.hostIfName = JVPPUtil.convertString(hostInterfaceName);
        return new FutureMapping<AfPacketDeleteReply, Void>(
                this.session().core().afPacketDelete(delete).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<Void> setInterfaceTag(InterfaceIndex index, Tag tag)
    {
        SwInterfaceTagAddDel req = new SwInterfaceTagAddDel();
        req.swIfIndex = index.getValue();
        req.isAdd = JVPPUtil.convertBoolean(tag != null);
        if (tag != null) req.tag = JVPPUtil.convertString(tag.getValue());
        return new FutureMapping<SwInterfaceTagAddDelReply, Void>(
                this.session().core().swInterfaceTagAddDel(req).toCompletableFuture(),
                (reply) -> null
        );
    }

    @Override
    public Future<Void> addInterfaceIPv4Address(InterfaceIndex index, IPv4CIDR address)
    {
        SwInterfaceAddDelAddress addr = new SwInterfaceAddDelAddress();
        addr.swIfIndex = Objects.requireNonNull(index).getValue();
        addr.isAdd = 1;
        addr.delAll = 0;
        addr.isIpv6 = 0;
        addr.addressLength = (byte) address.getMaskBits();
        addr.address = address.getAddress().getValue();
        return new FutureMapping<SwInterfaceAddDelAddressReply, Void>(
                this.session().core().swInterfaceAddDelAddress(addr).toCompletableFuture(),
                (reply) -> null
        );
    }

    @Override
    public Future<Void> removeInterfaceIPv4Address(InterfaceIndex index, IPv4CIDR address)
    {
        SwInterfaceAddDelAddress addr = new SwInterfaceAddDelAddress();
        addr.swIfIndex = Objects.requireNonNull(index).getValue();
        addr.isAdd = 0;
        addr.delAll = 0;
        addr.isIpv6 = 0;
        addr.addressLength = (byte) address.getMaskBits();
        addr.address = address.getAddress().getValue();
        return new FutureMapping<SwInterfaceAddDelAddressReply, Void>(
                this.session().core().swInterfaceAddDelAddress(addr).toCompletableFuture(),
                (reply) -> null
        );
    }

    @Override
    public Future<Void> removeInterfaceIPv4Addresses(InterfaceIndex index)
    {
        SwInterfaceAddDelAddress addr = new SwInterfaceAddDelAddress();
        addr.swIfIndex = Objects.requireNonNull(index).getValue();
        addr.isAdd = 0;
        addr.delAll = 1;
        addr.isIpv6 = 0;
        return new FutureMapping<SwInterfaceAddDelAddressReply, Void>(
                this.session().core().swInterfaceAddDelAddress(addr).toCompletableFuture(),
                (reply) -> null
        );
    }

    @Override
    public Future<Void> setInterfaceUp(InterfaceIndex index)
    {
        SwInterfaceSetFlags flags = new SwInterfaceSetFlags();
        flags.swIfIndex = Objects.requireNonNull(index).getValue();
        flags.adminUpDown = 1;
        return new FutureMapping<SwInterfaceSetFlagsReply, Void>(
                this.session().core().swInterfaceSetFlags(flags).toCompletableFuture(),
                (reply) -> null
        );
    }

    @Override
    public Future<Void> setInterfaceDown(InterfaceIndex index)
    {
        SwInterfaceSetFlags flags = new SwInterfaceSetFlags();
        flags.swIfIndex = Objects.requireNonNull(index).getValue();
        flags.adminUpDown = 0;
        return new FutureMapping<SwInterfaceSetFlagsReply, Void>(
                this.session().core().swInterfaceSetFlags(flags).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<Void> setInterfaceMTU(InterfaceIndex index, MTU mtu)
    {
        SwInterfaceSetMtu request = new SwInterfaceSetMtu();
        request.swIfIndex = Objects.requireNonNull(index).getValue();
        request.mtu = new int[] {mtu.getValue() & 0xFFFF, 0, 0, 0 };
        return new FutureMapping<SwInterfaceSetMtuReply, Void>(
                this.session().core().swInterfaceSetMtu(request).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<Void> setInterfaceMACAddress(InterfaceIndex index, MACAddress macAddress)
    {
        SwInterfaceSetMacAddress request = new SwInterfaceSetMacAddress();
        request.swIfIndex = Objects.requireNonNull(index).getValue();
        request.macAddress = Objects.requireNonNull(macAddress).getValue();
        return new FutureMapping<SwInterfaceSetMacAddressReply, Void>(
                this.session().core().swInterfaceSetMacAddress(request).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<InterfaceIndex> createLoopbackInterface(MACAddress macAddress)
    {
        CreateLoopback request = new CreateLoopback();
        request.macAddress = Objects.requireNonNull(macAddress).getValue();
        return new FutureMapping<CreateLoopbackReply, InterfaceIndex>(
                this.session().core().createLoopback(request).toCompletableFuture(),
                (reply) -> new InterfaceIndex(reply.swIfIndex)
        );
    }
    
    @Override
    public Future<Void> removeLoopbackInterface(InterfaceIndex interfaceIndex)
    {
        DeleteLoopback request = new DeleteLoopback();
        request.swIfIndex = Objects.requireNonNull(interfaceIndex).getValue();
        return new FutureMapping<DeleteLoopbackReply, Void>(
                this.session().core().deleteLoopback(request).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<InterfaceIndex> createVhostUserInterface(Path controlSocketFile, VhostUserMode mode, MACAddress mac)
    {
        CreateVhostUserIf create = new CreateVhostUserIf();
        create.sockFilename = JVPPUtil.convertString(Objects.requireNonNull(controlSocketFile.toAbsolutePath().toString()));
        create.isServer = JVPPUtil.convertBoolean(VhostUserMode.SERVER == mode);
        create.useCustomMac = JVPPUtil.convertBoolean(mac != null);
        if (mac != null) create.macAddress = mac.getValue();
        return new FutureMapping<CreateVhostUserIfReply, InterfaceIndex>(
                this.session().core().createVhostUserIf(create).toCompletableFuture(),
                (reply) -> new InterfaceIndex(reply.swIfIndex)
        );
    }
    
    @Override
    public Future<Void> removeVhostUserInterface(InterfaceIndex interfaceIndex)
    {
        DeleteVhostUserIf delete = new DeleteVhostUserIf();
        delete.swIfIndex = interfaceIndex.getValue();
        return new FutureMapping<DeleteVhostUserIfReply, Void>(
                this.session().core().deleteVhostUserIf(delete).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<Void> setInterfaceIPv4VRF(InterfaceIndex index, VRFIndex vrf)
    {
        SwInterfaceSetTable req = new SwInterfaceSetTable();
        req.isIpv6 = JVPPUtil.convertBoolean(false);
        req.swIfIndex = index.getValue();
        req.vrfId = vrf.getValue();
        return new FutureMapping<SwInterfaceSetTableReply, Void>(
                this.session().core().swInterfaceSetTable(req).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    // Helpers
    
    public static final InterfaceDetail interfaceDetail(SwInterfaceDetails detail)
    {
        InterfaceDetail iface = new InterfaceDetail();
        iface.setIndex(new InterfaceIndex(detail.swIfIndex));
        iface.setParentIndex(new InterfaceIndex(detail.supSwIfIndex));
        iface.setName(JVPPUtil.convertString(detail.interfaceName));
        iface.setAdminUp(detail.adminUpDown != 0);
        iface.setLinkUp(detail.linkUpDown != 0);
        iface.setMacAddress(new MACAddress(detail.l2Address));
        iface.setMtu(detail.linkMtu);
        iface.setTag(new Tag(JVPPUtil.convertString(detail.tag)));
        System.out.println(detail);
        return iface;
    }
}
