package com.intrbiz.virt.dash.router.admin;

import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequirePermission;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;

@Prefix("/admin/host")
@Template("layout/main")
@RequireValidPrincipal()
@RequirePermission("global_admin")
public class HostsRouter extends Router<VirtDashApp>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void images(VirtDB db)
    {
        var("hosts", app().getClusterManager().getHostStateStore().getHosts().stream().sorted().collect(Collectors.toList()));
        encode("admin/hosts");
    }
}
