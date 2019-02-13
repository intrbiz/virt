package com.intrbiz.vpp.core;

public class VPPModuleImpl
{
    protected final VPPSession session;
    
    public VPPModuleImpl(VPPSession session)
    {
        super();
        this.session = session;
    }
    
    protected VPPSession session()
    {
        return this.session;
    }
}
