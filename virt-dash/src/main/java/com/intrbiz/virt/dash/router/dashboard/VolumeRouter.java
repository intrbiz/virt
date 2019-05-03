package com.intrbiz.virt.dash.router.dashboard;

import java.io.IOException;
import java.util.UUID;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.CoalesceMode;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaBoolean;
import com.intrbiz.metadata.IsaLong;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.SessionVar;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Permission;
import com.intrbiz.virt.model.PersistentVolume;
import com.intrbiz.virt.model.Zone;

@Prefix("/volume")
@Template("layout/main")
@RequireValidPrincipal()
public class VolumeRouter extends Router<VirtDashApp>
{   
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void index(VirtDB db, @SessionVar("currentAccount") Account currentAccount)
    {
        var("volumes", db.getPersistentVolumesForAccount(currentAccount.getId()));
        // render
        encode("volume/index");
    }
    
    @Get("/new")
    @WithDataAdapter(VirtDB.class)
    public void newVolume(VirtDB db)
    {
        var("zones", db.listZones());
        encode("volume/new");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void requestVolume(
            VirtDB db,
            @SessionVar("currentAccount") Account currentAccount,
            @Param("zone") @IsaUUID UUID zoneId,
            @Param("name") @CheckStringLength(mandatory = true, min=3) String name,
            @Param("size") @IsaLong(min=1L, max=5000L, mandatory = true) long size,
            @Param("shared") @IsaBoolean(defaultValue = false, coalesce = CoalesceMode.ALWAYS) Boolean shared,
            @Param("description") String description
    ) throws IOException
    {
        Zone zone = notNull(db.getZone(zoneId));
        require(permission(Permission.STORAGE_MANAGE.toString(), currentAccount));
        sessionVar("currentVolume", new PersistentVolume(zone, currentAccount, name, size * 1000_000_000L, shared, description));
        redirect("/volume/finalise");
    }
    
    @Get("/finalise")
    public void finaliseVolume()
    {
        encode("volume/finalise");
    }
    
    @Post("/finalise")
    @WithDataAdapter(VirtDB.class)
    public void finaliseNetwork(VirtDB db) throws IOException
    {
        // the currently defined volume
        PersistentVolume volume = sessionVar("currentVolume");
        // add the network
        db.execute(() -> {
            db.setPersistentVolume(volume);
        });
        // fire the create network action
        action("volume.create", volume);
        // remove the vars
        sessionVar("currentVolume", null);
        redirect("/volume/");
    }
    
    @Any("/id/:id")
    @WithDataAdapter(VirtDB.class)
    public void volumeDetails(VirtDB db, @IsaUUID UUID id)
    {
       var("volume", notNull(db.getPersistentVolume(id)));
       encode("volume/details");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/**")
    public void newVolumeError()
    {
        encode("volume/new");
    }
}
