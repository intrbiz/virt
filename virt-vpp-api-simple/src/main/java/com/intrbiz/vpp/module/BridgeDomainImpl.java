package com.intrbiz.vpp.module;

import static com.intrbiz.vpp.util.JVPPUtil.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.intrbiz.vpp.api.model.BridgeDomainDetail;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.module.Bridge;
import com.intrbiz.vpp.core.FutureMapping;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;
import com.intrbiz.vpp.util.JVPPUtil;

import io.fd.vpp.jvpp.core.dto.BridgeDomainAddDel;
import io.fd.vpp.jvpp.core.dto.BridgeDomainAddDelReply;
import io.fd.vpp.jvpp.core.dto.BridgeDomainDetails;
import io.fd.vpp.jvpp.core.dto.BridgeDomainDetailsReplyDump;
import io.fd.vpp.jvpp.core.dto.BridgeDomainDump;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetL2Bridge;
import io.fd.vpp.jvpp.core.dto.SwInterfaceSetL2BridgeReply;

public class BridgeDomainImpl extends VPPModuleImpl implements Bridge
{
    public BridgeDomainImpl(VPPSession session)
    {
        super(session);
    }

    @Override
    public Future<List<BridgeDomainDetail>> listBridgeDomains()
    {
        BridgeDomainDump request = new BridgeDomainDump();
        request.bdId = 0xFFFFFFFF;
        return new FutureMapping<BridgeDomainDetailsReplyDump, List<BridgeDomainDetail>>(
                this.session().core().bridgeDomainDump(request).toCompletableFuture(),
                (reply) -> reply.bridgeDomainDetails.stream().map(BridgeDomainImpl::bridgeDomainDetail).collect(Collectors.toList())
        );
    }

    @Override
    public Future<Void> createBridgeDomain(BridgeDomainId id, boolean learn, boolean forward, boolean uuFlood, boolean flood, boolean arpTerm, int macAge, Tag bdTag)
    {
        BridgeDomainAddDel request = new BridgeDomainAddDel();
        request.isAdd = JVPPUtil.TRUE;
        request.bdId = id.getValue();
        request.learn = convertBoolean(learn);
        request.forward = convertBoolean(forward);
        request.uuFlood = convertBoolean(uuFlood);
        request.flood = convertBoolean(flood);
        request.arpTerm = convertBoolean(arpTerm);
        request.macAge = (byte) macAge;
        if (bdTag != null) request.bdTag = convertString(bdTag.getValue());
        return new FutureMapping<BridgeDomainAddDelReply, Void>(
                this.session().core().bridgeDomainAddDel(request).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<Void> removeBridgeDomain(BridgeDomainId id)
    {
        BridgeDomainAddDel request = new BridgeDomainAddDel();
        request.isAdd = JVPPUtil.FALSE;
        request.bdId = id.getValue();
        return new FutureMapping<BridgeDomainAddDelReply, Void>(
                this.session().core().bridgeDomainAddDel(request).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<Void> addInterfaceToBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex, boolean bridgeVirtualInterface, SplitHorizonGroup splitHorizonGroup)
    {
        SwInterfaceSetL2Bridge request = new SwInterfaceSetL2Bridge();
        request.bdId = Objects.requireNonNull(bridgeDomainId).getValue();
        request.rxSwIfIndex = Objects.requireNonNull(interfaceIndex).getValue();
        request.bvi = convertBoolean(bridgeVirtualInterface);
        request.enable = convertBoolean(true);
        if (splitHorizonGroup != null) request.shg = splitHorizonGroup.getValue();
        return new FutureMapping<SwInterfaceSetL2BridgeReply, Void>(
                this.session().core().swInterfaceSetL2Bridge(request).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    @Override
    public Future<Void> removeInterfaceFromBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex, boolean bridgeVirtualInterface, SplitHorizonGroup splitHorizonGroup)
    {
        SwInterfaceSetL2Bridge request = new SwInterfaceSetL2Bridge();
        request.bdId = Objects.requireNonNull(bridgeDomainId).getValue();
        request.rxSwIfIndex = Objects.requireNonNull(interfaceIndex).getValue();
        request.bvi = convertBoolean(bridgeVirtualInterface);
        request.enable = convertBoolean(false);
        if (splitHorizonGroup != null) request.shg = splitHorizonGroup.getValue();
        return new FutureMapping<SwInterfaceSetL2BridgeReply, Void>(
                this.session().core().swInterfaceSetL2Bridge(request).toCompletableFuture(),
                (reply) -> null
        );
    }
    
    public static BridgeDomainDetail bridgeDomainDetail(BridgeDomainDetails detail)
    {
        BridgeDomainDetail bridge = new BridgeDomainDetail();
        bridge.setId(new BridgeDomainId(detail.bdId));
        System.out.println(detail);
        return bridge;
    }
}
