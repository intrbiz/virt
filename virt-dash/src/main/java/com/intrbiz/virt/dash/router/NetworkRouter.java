package com.intrbiz.virt.dash.router;

import java.io.IOException;
import java.util.UUID;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CheckRegEx;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.SessionVar;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Network;
import com.intrbiz.virt.model.Permission;
import com.intrbiz.virt.model.Zone;

@Prefix("/network")
@Template("layout/main")
@RequireValidPrincipal()
public class NetworkRouter extends Router<App>
{   
    @Get("/new")
    @WithDataAdapter(VirtDB.class)
    public void newNetwork(VirtDB db)
    {
        var("zones", db.listZones());
        encode("network/new");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void requestAccount(
            VirtDB db,
            @SessionVar("currentAccount") Account currentAccount,
            @Param("zone") @IsaUUID UUID zoneId,
            @Param("name") @CheckStringLength(mandatory = true, min=3) String name,
            @Param("cidr") @CheckRegEx(mandatory = true, value="[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}/[0-9]{1,2}") String cidr,
            @Param("description") String description
    ) throws IOException
    {
        Zone zone = notNull(db.getZone(zoneId));
        require(permission(Permission.NETWORK_MANAGE.toString(), currentAccount));
        if (! Network.isCIDRUsable(cidr)) throw new BalsaValidationError("The provided CIDR is not usable");
        sessionVar("currentNetwork", new Network(zone, currentAccount, name, cidr, description));
        redirect("/network/finalise");
    }
    
    @Get("/finalise")
    public void finaliseNetwork()
    {
        encode("network/finalise");
    }
    
    @Post("/finalise")
    @WithDataAdapter(VirtDB.class)
    public void finaliseNetwork(VirtDB db) throws IOException
    {
        // the currently defined network
        Network network = sessionVar("currentNetwork");
        // add the network
        db.execute(() -> {
            db.setNetwork(network);
        });
        // fire the create network action
        action("network.create", network);
        // remove the vars
        sessionVar("currentNetwork", null);
        redirect("/");
    }
    
    @Any("/id/:id")
    @WithDataAdapter(VirtDB.class)
    public void networkDetails(VirtDB db, @IsaUUID UUID id)
    {
       var("network", notNull(db.getNetwork(id)));
       encode("network/details");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/**")
    public void requestAccountError()
    {
        encode("network/new");
    }
}
