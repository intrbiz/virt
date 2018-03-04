package com.intrbiz.virt.model;

import java.util.List;
import java.util.UUID;

import com.intrbiz.data.db.compiler.meta.Action;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLForeignKey;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.model.MachineType.EphemeralVolume;
import com.intrbiz.virt.util.IDUtil;

@SQLTable(schema = VirtDB.class, name = "machine", since = @SQLVersion({ 1, 0, 0 }))
public class Machine
{
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID accountId;

    @SQLColumn(index = 3, name = "type_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = MachineType.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID typeId;

    @SQLColumn(index = 4, name = "image_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = Image.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID imageId;

    @SQLColumn(index = 5, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "machine_name_unq", columns = { "account_id", "name" })
    private String name;

    @SQLColumn(index = 6, name = "cfg_mac", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "cfg_mac_unq")
    private String cfgMac;

    @SQLColumn(index = 7, name = "ssh_key_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = SSHKey.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID sshKeyId;

    @SQLColumn(index = 8, name = "description", since = @SQLVersion({ 1, 0, 0 }))
    private String description;

    @SQLColumn(index = 9, name = "metadata", type = "JSONB", since = @SQLVersion({ 1, 0, 0 }))
    private String metadata;
    
    @SQLColumn(index = 10, name = "zone_id", since = @SQLVersion({ 1, 0, 4 }))
    @SQLForeignKey(references = Zone.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 4 }))
    private UUID zoneId;

    public Machine()
    {
        super();
    }

    public Machine(Account account, Zone zone, MachineType type, Image image, String name, SSHKey sshKey, String description)
    {
        super();
        this.id = UUID.randomUUID();
        this.cfgMac = IDUtil.formatMac(IDUtil.randomMac());
        this.accountId = account.getId();
        this.zoneId = zone.getId();
        this.typeId = type.getId();
        this.imageId = image.getId();
        this.name = name;
        this.sshKeyId = sshKey.getId();
        this.description = description;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public UUID getTypeId()
    {
        return typeId;
    }

    public void setTypeId(UUID typeId)
    {
        this.typeId = typeId;
    }

    public UUID getImageId()
    {
        return imageId;
    }

    public void setImageId(UUID imageId)
    {
        this.imageId = imageId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCfgMac()
    {
        return cfgMac;
    }

    public void setCfgMac(String cfgMac)
    {
        this.cfgMac = cfgMac;
    }

    public UUID getSshKeyId()
    {
        return sshKeyId;
    }

    public void setSshKeyId(UUID sshKeyId)
    {
        this.sshKeyId = sshKeyId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }
    
    public UUID getZoneId()
    {
        return zoneId;
    }

    public void setZoneId(UUID zoneId)
    {
        this.zoneId = zoneId;
    }
    
    public Zone getZone()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getZone(this.zoneId);
        }
    }

    public MachineType getType()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineType(this.typeId);
        }
    }
    
    public Image getImage()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getImage(this.imageId);
        }
    }
    
    public SSHKey getSSHKey()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getSSHKey(this.sshKeyId);
        }
    }
    
    public List<MachineNIC> getInterfaces()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineNICsOfMachine(this.id);
        }
    }
    
    public List<MachineVolume> getVolumes()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineVolumesOfMachine(this.id);
        }
    }
    
    public Account getAccount()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getAccount(this.accountId);
        }
    }
    
    /**
     * Get the storage source path for this machines root volume
     */
    public String getSource()
    {
        return "zone/" + this.getZone().getName() + "/account/" + this.accountId + "/machine/" + this.id + ".sda";
    }
    
    public String getEphemeralVolumeSource(int evIndex)
    {
        return "zone/" + this.getZone().getName() + "/account/" + this.accountId + "/machine/" + this.id + "." + volumeName(evIndex);
    }
    
    public MachineEO toEvent()
    {
        Zone zone = this.getZone();
        MachineType type = this.getType();
        Image image = this.getImage();
        List<MachineVolume> volumes = this.getVolumes();
        List<MachineNIC> interfaces = this.getInterfaces();
        // machine basics
        MachineEO eo = new MachineEO(this.id, zone.getName(), this.name, type.getFamily(), type.getCpus(), type.getMemory(), this.cfgMac);
        // add the list of NICs to various networks
        for (MachineNIC nic : interfaces)
        {
            eo.getInterfaces().add(nic.toEvent());
        }
        // add the image volumes
        eo.getVolumes().add(MachineVolumeEO.clone(volumeName(0), image.getSource(), this.getSource(), image.getVolumeType()));
        // add the type volumes
        int evIndex = 1;
        for (EphemeralVolume volume : type.parseEphemeralVolumes())
        {
            eo.getVolumes().add(MachineVolumeEO.create(
                    volumeName(evIndex), 
                    this.getEphemeralVolumeSource(evIndex), 
                    volume.getSize(), 
                    volume.getVolumeType(), 
                    volume.toVolumeTypeMetadataString()));
            evIndex++;
        }
        // add the persistent volumes
        for (MachineVolume volume : volumes)
        {
            eo.getVolumes().add(volume.toEvent());
        }
        return eo;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Machine other = (Machine) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
    
    public String getInterfaceName(int interfaceIndex)
    {
        return interfaceName(interfaceIndex + 1);
    }
    
    public String getVolumeName(int persistentVolumeIndex)
    {
        return volumeName(persistentVolumeIndex + this.getType().getVolumeCount());
    }
    
    public static String interfaceName(int index)
    {
        return "eth" + ((char) (0x31 + index));
    }
    
    public static String volumeName(int deviceIndex)
    {
        return "sd" + ((char) (0x61 + deviceIndex));
    }
}
