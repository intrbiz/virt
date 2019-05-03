package com.intrbiz.virt.dash.router.admin;

import java.io.IOException;
import java.util.UUID;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequirePermission;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.DNSZone;
import com.intrbiz.virt.model.LoadBalancerPool;
import com.intrbiz.virt.model.LoadBalancerPoolTCPPort;
import com.intrbiz.virt.model.Network;

@Prefix("/admin/balancer/pool")
@Template("layout/main")
@RequireValidPrincipal()
@RequirePermission("global_admin")
public class LoadBalancerPoolRouter extends Router<VirtDashApp>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void zones(VirtDB db)
    {
        var("pools", db.listLoadBalancerPools());
        encode("admin/balancer/pools");
    }
    
    @Get("/new")
    @WithDataAdapter(VirtDB.class)
    public void newPool(VirtDB db)
    {
        var("networks", db.getSharedNetworks());
        encode("admin/balancer/new-pool");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void newPool(
            VirtDB db,
            @Param("name") @CheckStringLength(mandatory=true, min=3) String name,
            @Param("summary") @CheckStringLength(mandatory=true, min=3) String summary,
            @Param("description") @CheckStringLength() String description,
            @Param("network") @IsaUUID UUID networkId,
            @Param("endpoint") @CheckStringLength(mandatory=true, min=3) String endpoint
    ) throws IOException
    {
        Network network = notNull(db.getNetwork(networkId));
        db.setLoadBalancerPool(new LoadBalancerPool(network, name, summary, description, DNSZone.qualifyZoneName(endpoint)));
        redirect("/admin/balancer/pool/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/new")
    public void newPoolErrors()
    {
        encode("admin/balancer/new-pool");
    }
    
    @Any("/id/:id/remove")
    @WithDataAdapter(VirtDB.class)
    public void removePool(VirtDB db, @IsaUUID() UUID id) throws IOException
    {
        db.removeLoadBalancerPool(id);
        redirect("/admin/balancer/pool/");
    }
    
    @Get("/id/:id/add/tcp_ports")
    @WithDataAdapter(VirtDB.class)
    public void addTCPPorts(VirtDB db, @IsaUUID() UUID id)
    {
        var("pool", notNull(db.getLoadBalancerPool(id)));
        encode("admin/balancer/add-ports");
    }
    
    @Post("/id/:id/add/tcp_ports")
    @WithDataAdapter(VirtDB.class)
    public void doAddTCPPorts(
            VirtDB db, 
            @IsaUUID() UUID id,
            @Param("endpoint") @CheckStringLength(mandatory=true, min=3) String endpoint,
            @Param("bind") @CheckStringLength(mandatory=true, min=3) String bind,
            @Param("range") @CheckStringLength(mandatory=true, min=3) String range
    ) throws IOException
    {
        LoadBalancerPool pool = notNull(db.getLoadBalancerPool(id));
        final String qEndpoint = DNSZone.qualifyZoneName(endpoint);
        db.execute(() -> {
            for (String part : range.split(","))
            {
                int port = Integer.parseInt(part.trim());
                db.setLoadBalancerPoolTCPPort(new LoadBalancerPoolTCPPort(pool, port, qEndpoint, bind));
            }
        });
        redirect("/admin/balancer/pool/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/id/:id/add/tcp_ports")
    public void doAddTCPPortsErrors()
    {
        encode("admin/balancer/add-ports");
    }
}
