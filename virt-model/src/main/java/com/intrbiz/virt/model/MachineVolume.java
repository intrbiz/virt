package com.intrbiz.virt.model;

import java.util.UUID;

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
@SQLTable(schema = VirtDB.class, name = "machine_volume", since = @SQLVersion({ 1, 0, 0 }) )
public class MachineVolume
{
    @SQLColumn(index = 1, name = "machine_id", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLForeignKey(references = Machine.class, on = "id", onDelete = Action.CASCADE, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private UUID machineId;
    
    @SQLColumn(index = 2, name = "name", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private String name;
    
    @SQLColumn(index = 3, name = "size", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private long size;
    
    @SQLColumn(index = 4, name = "source", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLUnique(name = "source_unq")
    private String source;
    
    @SQLColumn(index = 5, name = "persistent_volume_id", since = @SQLVersion({ 1, 0, 6 }))
    @SQLForeignKey(references = PersistentVolume.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 6 }))
    private UUID persistentVolumeId;

    public MachineVolume()
    {
        super();
    }
    
    public MachineVolume(Machine machine, String name, PersistentVolume toAttach)
    {
        super();
        this.machineId = machine.getId();
        this.size = toAttach.getSize();
        this.persistentVolumeId = toAttach.getId();
        this.name = name;
        // compute the source
        this.source = toAttach.getSource();
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

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public UUID getPersistentVolumeId()
    {
        return persistentVolumeId;
    }

    public void setPersistentVolumeId(UUID persistentVolumeId)
    {
        this.persistentVolumeId = persistentVolumeId;
    }
    
    public Machine getMachine()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachine(this.machineId);
        }
    }
    
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
                MachineVolumeEO.deattach(this.name) : 
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
