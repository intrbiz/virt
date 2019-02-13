package com.intrbiz.vpp.module;

import com.intrbiz.vpp.api.module.NAT;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;

public class NATImpl extends VPPModuleImpl implements NAT
{
    public NATImpl(VPPSession session)
    {
        super(session);
    }
}
