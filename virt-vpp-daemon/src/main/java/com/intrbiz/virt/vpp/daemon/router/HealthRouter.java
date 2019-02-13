package com.intrbiz.virt.vpp.daemon.router;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.virt.VppDaemon;

@Prefix("/health")
public class HealthRouter extends Router<VppDaemon>
{
    @Any("/alive")
    @Text
    public String alive()
    {
        return "ALIVE\r\n";
    }
}
