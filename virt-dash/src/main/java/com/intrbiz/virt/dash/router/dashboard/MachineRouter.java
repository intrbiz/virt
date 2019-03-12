package com.intrbiz.virt.dash.router;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.intrbiz.Util;
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
import com.intrbiz.virt.model.Image;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.MachineType;
import com.intrbiz.virt.model.MachineVolume;
import com.intrbiz.virt.model.Network;
import com.intrbiz.virt.model.Permission;
import com.intrbiz.virt.model.PersistentVolume;
import com.intrbiz.virt.model.SSHKey;
import com.intrbiz.virt.model.Zone;

@Prefix("/machine")
@Template("layout/main")
@RequireValidPrincipal()
public class MachineRouter extends Router<VirtDashApp>
{    
    @Get("/new")
    @WithDataAdapter(VirtDB.class)
    public void newMachine(VirtDB db, @SessionVar("currentAccount") Account currentAccount)
    {
        var("zones", db.listZones());
        var("keys", db.getSSHKeysForAccount(currentAccount.getId()));
        var("images", db.getImagesForAccount(currentAccount.getId()));
        var("networks", db.getNetworksForAccount(currentAccount.getId()));
        var("types", db.listMachineTypes());
        encode("machine/new");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void newMachine(
            VirtDB db,
            @SessionVar("currentAccount") Account currentAccount,
            @Param("name") @CheckStringLength(mandatory = true, min=3) String name,
            @Param("zone") @IsaUUID UUID zoneId,
            @Param("type") @IsaUUID UUID typeId,
            @Param("image") @IsaUUID UUID imageId,
            @Param("network") @IsaUUID UUID networkId,
            @Param("key") @IsaUUID UUID keyId,
            @Param("description") String description,
            @Param("userData") String userData
    ) throws IOException
    {
        // can we create a machine
        require(permission(Permission.MACHINE_MANAGE.toString(), currentAccount));
        // look up the basic
        Zone zone = notNull(db.getZone(zoneId));
        MachineType type = notNull(db.getMachineType(typeId));
        Image image = notNull(db.getImage(imageId));
        Network network = notNull(db.getNetwork(networkId));
        SSHKey key = notNull(db.getSSHKey(keyId));
        // create the machine
        Machine machine = new Machine(currentAccount, zone, type, image, name, key, description);
        machine.setUserData(userData);
        // create the machine main NIC
        MachineNIC nic0 = new MachineNIC(machine, machine.getInterfaceName(0), network);
        // stash the details in the session
        sessionVar("currentMachine", machine);
        sessionVar("currentMachineNICs", new LinkedList<>(Arrays.asList(nic0)));
        sessionVar("currentMachineVolumes", new LinkedList<>());
        // confirm the details before we create it
        redirect("/machine/finalise");
    }
    
    @Get("/finalise")
    @WithDataAdapter(VirtDB.class)
    public void finaliseMachine(VirtDB db)
    {
        // the currently defined machine
        Machine machine = sessionVar("currentMachine");
        // volumes which can be attached
        var("volumes", db.getAvailablePersistentVolumesForAccountInZone(machine.getAccountId(), machine.getZoneId()));
        // networks which can be attached
        var("networks", db.getNetworksForAccountInZone(machine.getAccountId(), machine.getZoneId()));
        encode("machine/finalise");
    }
    
    @Post("/finalise")
    @WithDataAdapter(VirtDB.class)
    public void doFinaliseMachine(VirtDB db) throws IOException
    {
        // the currently defined machine
        Machine machine = sessionVar("currentMachine");
        List<MachineNIC> nics = sessionVar("currentMachineNICs");
        List<MachineVolume> vols = sessionVar("currentMachineVolumes");
        // update NIC IP addresses
        for (MachineNIC nic : nics)
        {
            String ipv4 = param(nic.getName() + "_ipv4");
            if (! Util.isEmpty(ipv4))
                nic.setIpv4(ipv4);
        }
        // add the machine
        db.execute(() -> {
            db.setMachine(machine);
            for (MachineNIC nic : nics)
            {
                db.setMachineNIC(nic);
            }
            for (MachineVolume vol : vols)
            {
                db.setMachineVolume(vol);
            }
        });
        // fire the create machine action
        action("machine.create", machine);
        // remove the vars
        sessionVar("currentMachine", null);
        sessionVar("currentMachineNICs", null);
        sessionVar("currentMachineVolumes", null);
        redirect("/");
    }
    
    @Post("/finalise/volume/attach")
    @WithDataAdapter(VirtDB.class)
    public void doAttachVolume(
            VirtDB db,
            @Param("volume") @IsaUUID() UUID volumeId
    ) throws IOException
    {
        // the volume to attach
        PersistentVolume volume = notNull(db.getPersistentVolume(volumeId));
        // current state
        Machine machine = sessionVar("currentMachine");
        List<MachineVolume> vols = sessionVar("currentMachineVolumes");
        // attach the volume
        vols.add(new MachineVolume(machine, machine.getVolumeName(vols.size()), volume));
        // update the state
        sessionVar("currentMachineVolumes", vols);
        redirect("/machine/finalise");
    }
    
    @Post("/finalise/network/attach")
    @WithDataAdapter(VirtDB.class)
    public void doAttachNetwork(
            VirtDB db,
            @Param("network") @IsaUUID() UUID networkId
    ) throws IOException
    {
        // the volume to attach
        Network network = notNull(db.getNetwork(networkId));
        // current state
        Machine machine = sessionVar("currentMachine");
        List<MachineNIC> nics = sessionVar("currentMachineNICs");
        // attach the volume
        nics.add(new MachineNIC(machine, machine.getInterfaceName(nics.size()), network));
        // update the state
        sessionVar("currentMachineNICs", nics);
        redirect("/machine/finalise");
    }
    
    @Any("/id/:id")
    @WithDataAdapter(VirtDB.class)
    public void machineDetails(VirtDB db, @IsaUUID UUID id)
    {
       var("machine", notNull(db.getMachine(id)));
       encode("machine/details");
    }
    
    @Any("/id/:id/reboot")
    @WithDataAdapter(VirtDB.class)
    public void rebootMachine(VirtDB db, @IsaUUID UUID id, @Param("force") @IsaBoolean(defaultValue = false, coalesce = CoalesceMode.ALWAYS) Boolean force) throws IOException
    {
        action("machine.reboot", notNull(db.getMachine(id)), force);
        redirect("/");
    }
    
    @Any("/id/:id/start")
    @WithDataAdapter(VirtDB.class)
    public void startMachine(VirtDB db, @IsaUUID UUID id) throws IOException
    {
        action("machine.start", notNull(db.getMachine(id)));
        redirect("/");
    }
    
    @Any("/id/:id/stop")
    @WithDataAdapter(VirtDB.class)
    public void stopMachine(VirtDB db, @IsaUUID UUID id, @Param("force") @IsaBoolean(defaultValue = false, coalesce = CoalesceMode.ALWAYS) Boolean force) throws IOException
    {
        action("machine.stop", notNull(db.getMachine(id)), force);
        redirect("/");
    }
    
    @Any("/id/:id/release")
    @WithDataAdapter(VirtDB.class)
    public void releaseMachine(VirtDB db, @IsaUUID UUID id) throws IOException
    {
        action("machine.release", notNull(db.getMachine(id)));
        redirect("/");
    }
    
    @Any("/id/:id/terminate")
    @WithDataAdapter(VirtDB.class)
    public void terminateMachine(VirtDB db, @IsaUUID UUID id) throws IOException
    {
        action("machine.terminate", notNull(db.getMachine(id)));
        redirect("/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/**")
    public void newMachineError()
    {
        encode("machine/new");
    }
}
