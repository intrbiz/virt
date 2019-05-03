package com.intrbiz.virt.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.data.db.compiler.meta.Action;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLForeignKey;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.event.model.MachineAdminStatus;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.model.MachineType.EphemeralVolume;
import com.intrbiz.virt.util.IDUtil;
import com.intrbiz.virt.util.NameUtil;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("machine")
@SQLTable(schema = VirtDB.class, name = "machine", since = @SQLVersion({ 1, 0, 0 }))
public class Machine
{
    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @JsonProperty("account_id")
    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID accountId;

    @JsonIgnore
    @SQLColumn(index = 3, name = "type_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = MachineType.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID typeId;

    @JsonIgnore
    @SQLColumn(index = 4, name = "image_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = Image.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID imageId;

    @JsonProperty("name")
    @SQLColumn(index = 5, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "machine_name_unq", columns = { "account_id", "name" })
    private String name;

    @JsonProperty("config_mac")
    @SQLColumn(index = 6, name = "cfg_mac", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "cfg_mac_unq")
    private String cfgMac;

    @JsonIgnore
    @SQLColumn(index = 7, name = "ssh_key_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = SSHKey.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID sshKeyId;

    @JsonProperty("description")
    @SQLColumn(index = 8, name = "description", since = @SQLVersion({ 1, 0, 0 }))
    private String description;

    @JsonIgnore
    @SQLColumn(index = 9, name = "metadata", type = "JSONB", since = @SQLVersion({ 1, 0, 0 }))
    private String metadata;
    
    @JsonIgnore
    @SQLColumn(index = 10, name = "zone_id", since = @SQLVersion({ 1, 0, 4 }))
    @SQLForeignKey(references = Zone.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 4 }))
    private UUID zoneId;
    
    @JsonProperty("admin_status")
    @SQLColumn(index = 11, name = "admin_status", since = @SQLVersion({ 1, 0, 9 }))
    private MachineAdminStatus adminStatus;
    
    @JsonProperty("user_data")
    @SQLColumn(index = 12, name = "user_data", since = @SQLVersion({ 1, 0, 10 }))
    private String userData;
    
    @JsonProperty("config_ipv4")
    @SQLColumn(index = 6, name = "cfg_ipv4", notNull = true, since = @SQLVersion({ 1, 0, 19 }))
    @SQLUnique(name = "cfg_ipv4_unq")
    private String cfgIPv4;
    
    @JsonProperty("placement_rule")
    @SQLColumn(index = 6, name = "placement_rule", since = @SQLVersion({ 1, 0, 28 }))
    private String placementRule;

    public Machine()
    {
        super();
    }

    public Machine(Account account, Zone zone, MachineType type, Image image, String name, SSHKey sshKey, String description, String placementRule)
    {
        super();
        this.id = account.randomObjectId();
        this.cfgMac = IDUtil.formatMac(IDUtil.randomMac());
        this.cfgIPv4 = IDUtil.randomCfgAddr();
        this.accountId = account.getId();
        this.zoneId = zone.getId();
        this.typeId = type.getId();
        this.imageId = image.getId();
        this.name = NameUtil.toSafeName(name);
        this.sshKeyId = sshKey.getId();
        this.description = description;
        this.placementRule = placementRule;
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

    public String getCfgIPv4()
    {
        return cfgIPv4;
    }

    public void setCfgIPv4(String cfgIPv4)
    {
        this.cfgIPv4 = cfgIPv4;
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
    
    public MachineAdminStatus getAdminStatus()
    {
        return adminStatus;
    }

    public void setAdminStatus(MachineAdminStatus adminStatus)
    {
        this.adminStatus = adminStatus;
    }

    public String getUserData()
    {
        return userData;
    }

    public void setUserData(String userData)
    {
        this.userData = userData;
    }

    public String getPlacementRule()
    {
        return placementRule;
    }

    public void setPlacementRule(String placementRule)
    {
        this.placementRule = placementRule;
    }

    @JsonProperty("zone")
    public Zone getZone()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getZone(this.zoneId);
        }
    }

    @JsonProperty("machine_type")
    public MachineType getType()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineType(this.typeId);
        }
    }
    
    @JsonProperty("image")
    public Image getImage()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getImage(this.imageId);
        }
    }
    
    @JsonProperty("ssh_key_set")
    public SSHKey getSSHKey()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getSSHKey(this.sshKeyId);
        }
    }
    
    @JsonProperty("interfaces")
    public List<MachineNIC> getInterfaces()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineNICsOfMachine(this.id);
        }
    }
    
    @JsonProperty("volumes")
    public List<MachineVolume> getVolumes()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineVolumesOfMachine(this.id);
        }
    }
    
    @JsonIgnore
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
    @JsonIgnore
    public String getSource()
    {
        return "z_" + this.getZone().getName() + "_a_" + this.accountId + "_m_" + this.id + ".sda";
    }
    
    public String getEphemeralVolumeSource(int evIndex)
    {
        return "z_" + this.getZone().getName() + "_a_" + this.accountId + "_m_" + this.id + "." + volumeName(evIndex);
    }
    
    public MachineEO toEvent()
    {
        Account account = this.getAccount();
        Zone zone = this.getZone();
        MachineType type = this.getType();
        Image image = this.getImage();
        List<MachineVolume> volumes = this.getVolumes();
        List<MachineNIC> interfaces = this.getInterfaces();
        // machine basics
        MachineEO eo = new MachineEO(this.id, zone.getName(), this.name, type.getFamily(), type.getName(), type.getCpus(), type.getMemory(), this.cfgMac, this.cfgIPv4, this.adminStatus, this.placementRule, account.toEvent());
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
                    volume.getVolumeTypeMetadata()));
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
        return "eth" + ((char) (0x30 + index));
    }
    
    public static String volumeName(int deviceIndex)
    {
        return "sd" + ((char) (0x61 + deviceIndex));
    }
}
