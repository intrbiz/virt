package com.intrbiz.virt.dash.router.internal;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.virt.VirtDashApp;

public abstract class InternalRouter extends Router<VirtDashApp>
{   
    public void before()
    {
        require("127.0.0.1".equals(request().getRemoteAddress()) || request().getRemoteAddress().startsWith("10.10.") );
    }
}

