package com.intrbiz.virt.dash.router.admin;

import java.io.IOException;
import java.util.UUID;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaInt;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequirePermission;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Network;

@Prefix("/admin/network")
@Template("layout/main")
@RequireValidPrincipal()
@RequirePermission("global_admin")
public class NetworksRouter extends Router<VirtDashApp>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void networks(VirtDB db)
    {
        var("networks", db.getSharedNetworks());
        encode("admin/networks");
    }
    
    @Get("/id/:id")
    @WithDataAdapter(VirtDB.class)
    public void showNetworkDetails(VirtDB db, @IsaUUID UUID networkId)
    {
        var("network", db.getNetwork(networkId));
        encode("admin/network-details");
    }
    
    @Get("/id/:id/destroy")
    @WithDataAdapter(VirtDB.class)
    public void destroyNetwork(VirtDB db, @IsaUUID UUID networkId) throws IOException
    {
        db.execute(() -> {
            db.removeNetwork(networkId);
        });
        redirect("/admin/network/");
    }
    
    @Get("/new")
    @WithDataAdapter(VirtDB.class)
    public void showNewNetwork(VirtDB db)
    {
        var("zones", db.listZones());
        encode("admin/new-network");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void doNewNetwork(
            VirtDB db,
            @Param("name") String name, 
            @Param("zone") @IsaUUID UUID zoneId,
            @Param("cidr") String cidr,
            @Param("vxlanid") @IsaInt() int vxlanId,
            @Param("purpose") String purpose,
            @Param("description") String description
    ) throws IOException
    {
        Network net = new Network(db.getZone(zoneId), null, name, cidr, vxlanId, description);
        if (! "none".equals(purpose)) net.setPurpose(purpose);
        db.execute(() -> {
            db.setNetwork(net);
        });
        redirect("/admin/network/");
    }
}
