package com.intrbiz.vpp.module;

import com.intrbiz.vpp.api.module.ACL;
import com.intrbiz.vpp.core.VPPModuleImpl;
import com.intrbiz.vpp.core.VPPSession;

public class ACLImpl extends VPPModuleImpl implements ACL
{
    public ACLImpl(VPPSession session)
    {
        super(session);
    }
}
