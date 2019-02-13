package com.intrbiz.virt.metadata.router;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.virt.VirtHostApp;

@Prefix("/health/")
public class HealthRouter extends Router<VirtHostApp>
{    
    @Any("/self")
    @Text
    public String selfHealth()
    {
        return "OK";
    }
}