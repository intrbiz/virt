package com.intrbiz.virt.model;

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
import com.intrbiz.virt.event.model.MachineVolumeEO;

/**
 * Persistent volumes attached to a Machine
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("machine.volume")
@SQLTable(schema = VirtDB.class, name = "machine_volume", since = @SQLVersion({ 1, 0, 0 }) )
public class MachineVolume
{
    @JsonProperty("machine_id")
    @SQLColumn(index = 1, name = "machine_id", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLForeignKey(references = Machine.class, on = "id", onDelete = Action.CASCADE, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private UUID machineId;
    
    @JsonProperty("name")
    @SQLColumn(index = 2, name = "name", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private String name;
    
    @JsonProperty("persistent_volume_id")
    @SQLColumn(index = 3, name = "persistent_volume_id", since = @SQLVersion({ 1, 0, 6 }))
    @SQLForeignKey(references = PersistentVolume.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 6 }))
    @SQLUnique(name = "persistent_volume_unq", columns = { "machine_id", "persistent_volume_id" })
    private UUID persistentVolumeId;

    public MachineVolume()
    {
        super();
    }
    
    public MachineVolume(Machine machine, String name, PersistentVolume toAttach)
    {
        super();
        this.machineId = machine.getId();
        this.persistentVolumeId = toAttach.getId();
        this.name = name;
    }

    public UUID getMachineId()
    {
        return machineId;
    }

    public void setMachineId(UUID machineId)
    {
        this.machineId = machineId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public UUID getPersistentVolumeId()
    {
        return persistentVolumeId;
    }

    public void setPersistentVolumeId(UUID persistentVolumeId)
    {
        this.persistentVolumeId = persistentVolumeId;
    }
    
    @JsonIgnore
    public Machine getMachine()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachine(this.machineId);
        }
    }
    
    @JsonIgnore
    public PersistentVolume getAttached()
    {
        if (this.persistentVolumeId == null) return null;
        try (VirtDB db = VirtDB.connect())
        {
            return db.getPersistentVolume(this.getPersistentVolumeId());
        }
    }
    
    public boolean isAttached()
    {
        return this.persistentVolumeId != null;
    }
    
    public MachineVolumeEO toEvent()
    {
        PersistentVolume attached = this.getAttached();
        return attached == null ? 
                MachineVolumeEO.detach(this.name) : 
                MachineVolumeEO.attach(this.name, attached.getSource(), attached.getVolumeType());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((machineId == null) ? 0 : machineId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MachineVolume other = (MachineVolume) obj;
        if (machineId == null)
        {
            if (other.machineId != null) return false;
        }
        else if (!machineId.equals(other.machineId)) return false;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }
}
