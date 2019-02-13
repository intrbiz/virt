package com.intrbiz.vpp.module;

import java.util.concurrent.Future;

import com.intrbiz.vpp.api.module.Info;
import com.intrbiz.vpp.core.FutureMapping;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;
import com.intrbiz.vpp.util.JVPPUtil;

import io.fd.vpp.jvpp.core.dto.ShowVersion;
import io.fd.vpp.jvpp.core.dto.ShowVersionReply;

public class InfoImpl extends VPPModuleImpl implements Info
{
    public InfoImpl(VPPSession session)
    {
        super(session);
    }

    @Override
    public Future<String> getVersion()
    {
        return new FutureMapping<ShowVersionReply, String>(
                this.session().core().showVersion(new ShowVersion()).toCompletableFuture(), 
                (reply) -> JVPPUtil.convertString(reply.program) + " " + JVPPUtil.convertString(reply.version) + " @ " + JVPPUtil.convertString(reply.buildDate)
        );
    }
}
