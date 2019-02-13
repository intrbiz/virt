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
import com.intrbiz.virt.event.model.PersistentVolumeEO;

@SQLTable(schema = VirtDB.class, name = "persistent_volume", since = @SQLVersion({ 1, 0, 6 }) )
public class PersistentVolume
{
    public static interface TYPE
    {
        public static final String CEPH = "ceph";
    }
    
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 6 }) )
    @SQLPrimaryKey()
    private UUID id;
    
    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 6 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 6 }))
    private UUID accountId;
    
    @SQLColumn(index = 3, name = "name", since = @SQLVersion({ 1, 0, 6 }) )
    @SQLUnique(name = "volume_name_unq", columns = { "account_id", "name" })
    private String name;
    
    @SQLColumn(index = 4, name = "size", notNull = true, since = @SQLVersion({ 1, 0, 6 }))
    private long size;
    
    @SQLColumn(index = 5, name = "description", since = @SQLVersion({ 1, 0, 6 }) )
    private String description;
    
    @SQLColumn(index = 6, name = "volume_type", since = @SQLVersion({ 1, 0, 6 }) )
    private String volumeType;
    
    @SQLColumn(index = 7, name = "metadata", type = "JSONB", since = @SQLVersion({ 1, 0, 6 }) )
    private String metadata;
    
    @SQLColumn(index = 8, name = "zone_id", since = @SQLVersion({ 1, 0, 4 }))
    @SQLForeignKey(references = Zone.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 6 }))
    private UUID zoneId;
    
    @SQLColumn(index = 9, name = "shared", since = @SQLVersion({ 1, 0, 7 }) )
    private boolean shared;

    public PersistentVolume()
    {
        super();
    }
    
    public PersistentVolume(Zone zone, Account account, String name, long size, boolean shared, String description)
    {
        super();
        this.id = UUID.randomUUID();
        this.zoneId = zone.getId();
        this.accountId = account.getId();
        this.name = name;
        this.size = size;
        this.shared = shared;
        this.description = description;
        this.volumeType = TYPE.CEPH;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getVolumeType()
    {
        return volumeType;
    }

    public void setVolumeType(String volumeType)
    {
        this.volumeType = volumeType;
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
    
    public boolean isShared()
    {
        return shared;
    }

    public void setShared(boolean shared)
    {
        this.shared = shared;
    }

    public Zone getZone()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getZone(this.zoneId);
        }
    }
    
    public List<MachineVolume> getAttachments()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineVolumesAttachedTo(this.id);
        }
    }
    
    public boolean isAttached()
    {
        return ! this.getAttachments().isEmpty();
    }
    
    public String getSource()
    {
        return "z/" + this.getZone().getName() + "/a/" + this.accountId + "/v/" + this.id;
    }
    
    public PersistentVolumeEO toEvent()
    {
        PersistentVolumeEO eo = new PersistentVolumeEO();
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
        PersistentVolume other = (PersistentVolume) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
}
