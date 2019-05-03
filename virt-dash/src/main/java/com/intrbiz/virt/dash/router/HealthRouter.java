package com.intrbiz.virt.dash.router;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.virt.VirtDashApp;

@Prefix("/health/")
public class HealthRouter extends Router<VirtDashApp>
{    
    @Any("/alive")
    @Text
    public String alive()
    {
        return "OK";
    }
}