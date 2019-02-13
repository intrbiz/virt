package com.intrbiz.virt.vpp.daemon.router;

import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;

@Prefix("/version")
public class VersionRouter extends VppBaseRouter
{
    @Any("/vpp")
    @Text
    public String vppVersion() throws Exception
    {
        return info().getVersion().get();
    }
}
