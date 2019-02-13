package com.intrbiz.vpp.module;

import java.util.concurrent.Future;

import com.intrbiz.vpp.api.model.VRFIndex;
import com.intrbiz.vpp.api.module.Routing;
import com.intrbiz.vpp.core.FutureMapping;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;
import com.intrbiz.vpp.util.JVPPUtil;

import io.fd.vpp.jvpp.core.dto.IpTableAddDel;
import io.fd.vpp.jvpp.core.dto.IpTableAddDelReply;

public class RoutingImpl extends VPPModuleImpl implements Routing
{
    public RoutingImpl(VPPSession session)
    {
        super(session);
    }

    @Override
    public Future<Void> createIPv4VRF(VRFIndex index, String name)
    {
        IpTableAddDel add = new IpTableAddDel();
        add.isAdd = JVPPUtil.TRUE;
        add.isIpv6 = JVPPUtil.FALSE;
        add.tableId = index.getValue();
        add.name = JVPPUtil.convertString(name);
        return new FutureMapping<IpTableAddDelReply, Void>(
                session().core().ipTableAddDel(add).toCompletableFuture(),
                (reply) -> null
        );
    }

    @Override
    public Future<Void> removeIPv4VRF(VRFIndex index)
    {
        IpTableAddDel del = new IpTableAddDel();
        del.isAdd = JVPPUtil.FALSE;
        del.isIpv6 = JVPPUtil.FALSE;
        del.tableId = index.getValue();
        return new FutureMapping<IpTableAddDelReply, Void>(
                session().core().ipTableAddDel(del).toCompletableFuture(),
                (reply) -> null
        );
    }
}
