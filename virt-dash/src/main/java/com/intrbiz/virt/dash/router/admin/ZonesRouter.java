package com.intrbiz.virt.dash.router.admin;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.dash.model.RunningZone;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Zone;
import com.intrbiz.virt.scheduler.SchedulerManager;

@Prefix("/admin/zone")
@Template("layout/main")
@RequireValidPrincipal()
@RequirePermission("global_admin")
public class ZonesRouter extends Router<App>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void zones(VirtDB db)
    {
        SchedulerManager schedulerManager = app().getClusterManager().getSchedulerManager();
        var("zones", db.listZones().stream()
                .map((z) -> new RunningZone(z, schedulerManager.getZoneSchedulerState(z.getName())))
                .collect(Collectors.toList()));
        encode("admin/zones");
    }
    
    @Get("/new")
    public void newZone()
    {
        encode("admin/new-zone");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void newZone(
            VirtDB db,
            @Param("name") @CheckStringLength(mandatory=true, min=3) String name,
            @Param("summary") @CheckStringLength(mandatory=true, min=3) String summary,
            @Param("description") @CheckStringLength() String description
    ) throws IOException
    {
        db.setZone(new Zone(name, summary, description));
        redirect("/admin/zone/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/new")
    public void newZoneErrors()
    {
        encode("admin/new-zone");
    }
    
    @Any("/id/:id/remove")
    @WithDataAdapter(VirtDB.class)
    public void removeZone(VirtDB db, @IsaUUID() UUID id) throws IOException
    {
        db.removeZone(id);
        redirect("/admin/zone/");
    }
}
