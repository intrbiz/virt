package com.intrbiz.vpp.module;

import java.util.concurrent.Future;

import com.intrbiz.vpp.api.module.Graph;
import com.intrbiz.vpp.core.FutureMapping;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;
import com.intrbiz.vpp.util.JVPPUtil;

import io.fd.vpp.jvpp.core.dto.GetNodeIndex;
import io.fd.vpp.jvpp.core.dto.GetNodeIndexReply;

public class GraphImpl extends VPPModuleImpl implements Graph
{
    public GraphImpl(VPPSession session)
    {
        super(session);
    }

    @Override
    public Future<Integer> nodeIndex(String nodeName)
    {
        GetNodeIndex request = new GetNodeIndex();
        request.nodeName = JVPPUtil.convertString(nodeName);
        return new FutureMapping<GetNodeIndexReply, Integer>(
                this.session().core().getNodeIndex(request).toCompletableFuture(),
                (reply) -> reply.nodeIndex
        );
    }

    
}
