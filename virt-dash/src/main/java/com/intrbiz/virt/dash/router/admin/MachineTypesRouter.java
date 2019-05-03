package com.intrbiz.virt.dash.router.admin;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.CoalesceMode;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaInt;
import com.intrbiz.metadata.IsaLong;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.ListParam;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequirePermission;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.MachineType;
import com.intrbiz.virt.model.MachineType.EphemeralVolume;
import com.intrbiz.virt.model.MachineTypeFamily;

@Prefix("/admin/machine-type")
@Template("layout/main")
@RequireValidPrincipal()
@RequirePermission("global_admin")
public class MachineTypesRouter extends Router<VirtDashApp>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void machineType(VirtDB db)
    {
        var("machineTypes", db.listMachineTypes());
        var("machineTypeFamilies", db.listMachineTypeFamilies());
        encode("admin/machine-type/index");
    }
    
    @Get("/new")
    @WithDataAdapter(VirtDB.class)
    public void newMachineType(VirtDB db)
    {
        var("machineTypeFamilies", db.listMachineTypeFamilies());
        encode("admin/machine-type/new");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void newMachineType(
            VirtDB db,
            @Param("family") @CheckStringLength(mandatory=true, min=1) String family,
            @Param("name") @CheckStringLength(mandatory=true, min=3) String name,
            @Param("cpus") @IsaInt(mandatory=true) int cpus,
            @Param("memory") @IsaLong(mandatory=true) long memory,
            @Param("volume_limit") @IsaInt(mandatory=false, defaultValue=1, coalesce=CoalesceMode.ON_NULL) int volumeLimit,
            @ListParam("volume_types") List<String> volumeTypes,
            @Param("nic_limit") @IsaInt(mandatory=false, defaultValue=1, coalesce=CoalesceMode.ON_NULL) int nicLimit,
            @ListParam("network_types") List<String> networkTypes,
            @Param("ephemeral_volumes") @CheckStringLength() String ephemeralVolumes,
            @Param("summary") @CheckStringLength(mandatory=false) String summary,
            @Param("description") @CheckStringLength(mandatory=false) String description
    ) throws IOException
    {
        MachineType type = new MachineType(family, name);
        type.setCpus(cpus);
        type.setMemory(memory * 1024L * 1024L);
        type.setNicLimit(nicLimit);
        type.setVolumeLimit(volumeLimit);
        type.setSupportedVolumeTypes(volumeTypes);
        type.setSupportedNetworkTypes(networkTypes);
        type.setEphemeralVolumes(EphemeralVolume.parse(ephemeralVolumes).stream().map(EphemeralVolume::toString).collect(Collectors.toList()));
        type.setSummary(summary);
        type.setDescription(description);
        db.setMachineType(type);
        redirect("/admin/machine-type/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/new")
    public void newMachineTypeErrors()
    {
        encode("admin/machine-type/new");
    }
    
    @Get("/id/:id/edit")
    @WithDataAdapter(VirtDB.class)
    public void editMachineType(VirtDB db, @IsaUUID UUID id)
    {
        var("machineType", notNull(db.getMachineType(id)));
        encode("admin/machine-type/edit");
    }
    
    @Post("/id/:id/edit")
    @WithDataAdapter(VirtDB.class)
    public void editMachineType(
            VirtDB db,
            @IsaUUID UUID id,
            @Param("cpus") @IsaInt(mandatory=true) int cpus,
            @Param("memory") @IsaLong(mandatory=true) long memory,
            @Param("volume_limit") @IsaInt(mandatory=false, defaultValue=1, coalesce=CoalesceMode.ON_NULL) int volumeLimit,
            @ListParam("volume_types") List<String> volumeTypes,
            @Param("nic_limit") @IsaInt(mandatory=false, defaultValue=1, coalesce=CoalesceMode.ON_NULL) int nicLimit,
            @ListParam("network_types") List<String> networkTypes,
            @Param("ephemeral_volumes") @CheckStringLength() String ephemeralVolumes,
            @Param("summary") @CheckStringLength(mandatory=false) String summary,
            @Param("description") @CheckStringLength(mandatory=false) String description
    ) throws IOException
    {
        MachineType type = var("machineType", notNull(db.getMachineType(id)));
        type.setCpus(cpus);
        type.setMemory(memory * 1024L * 1024L);
        type.setNicLimit(nicLimit);
        type.setVolumeLimit(volumeLimit);
        type.setSupportedVolumeTypes(volumeTypes);
        type.setSupportedNetworkTypes(networkTypes);
        type.setEphemeralVolumes(EphemeralVolume.parse(ephemeralVolumes).stream().map(EphemeralVolume::toString).collect(Collectors.toList()));
        type.setSummary(summary);
        type.setDescription(description);
        db.setMachineType(type);
        redirect("/admin/machine-type/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/id/:id/edit")
    public void editMachineTypeErrors()
    {
        encode("admin/machine-type/edit");
    }
    
    @Any("/id/:id/remove")
    @WithDataAdapter(VirtDB.class)
    public void removeMachineType(VirtDB db, @IsaUUID() UUID id) throws IOException
    {
        db.removeMachineType(id);
        redirect("/admin/machine-type/");
    }
    
    @Get("/family/new")
    public void newMachineTypeFamily()
    {
        encode("admin/machine-type/family/new");
    }
    
    @Post("/family/new")
    @WithDataAdapter(VirtDB.class)
    public void newMachineTypeFamily(
            VirtDB db,
            @Param("family") @CheckStringLength(mandatory=true, min=1) String family,
            @Param("summary") @CheckStringLength(mandatory=false) String summary,
            @Param("description") @CheckStringLength(mandatory=false) String description
    ) throws IOException
    {
        MachineTypeFamily type = new MachineTypeFamily(family);
        type.setSummary(summary);
        type.setDescription(description);
        db.setMachineTypeFamily(type);
        redirect("/admin/machine-type/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/family/new")
    public void newMachineTypeFamilyErrors()
    {
        encode("admin/machine-type/family/new");
    }
    
    @Any("/family/:family/remove")
    @WithDataAdapter(VirtDB.class)
    public void removeMachineTypeFamily(VirtDB db, String id) throws IOException
    {
        db.removeMachineTypeFamily(id);
        redirect("/admin/machine-type/");
    }
    
    @Get("/family/:family/edit")
    @WithDataAdapter(VirtDB.class)
    public void editMachineTypeFamily(VirtDB db, String family)
    {
        var("machineTypeFamily", notNull(db.getMachineTypeFamily(family)));
        encode("admin/machine-type/family/edit");
    }
    
    @Post("/family/:family/edit")
    @WithDataAdapter(VirtDB.class)
    public void editMachineTypeFamily(
            VirtDB db,
            String family,
            @Param("summary") @CheckStringLength(mandatory=false) String summary,
            @Param("description") @CheckStringLength(mandatory=false) String description
    ) throws IOException
    {
        MachineTypeFamily type = var("machineTypeFamily", notNull(db.getMachineTypeFamily(family)));
        type.setSummary(summary);
        type.setDescription(description);
        db.setMachineTypeFamily(type);
        redirect("/admin/machine-type/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/family/:family/edit")
    public void editMachineTypeFamilyErrors()
    {
        encode("admin/machine-type/family/edit");
    }
}
