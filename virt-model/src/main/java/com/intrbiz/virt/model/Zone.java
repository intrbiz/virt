package com.intrbiz.virt.model;

import java.util.UUID;

import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@SQLTable(schema = VirtDB.class, name = "zone", since = @SQLVersion({ 1, 0, 4 }) )
public class Zone
{
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 4 }) )
    @SQLPrimaryKey()
    private UUID id;
    
    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 4 }) )
    @SQLUnique(name = "name_unq")
    private String name;
    
    @SQLColumn(index = 4, name = "summary", notNull = true, since = @SQLVersion({ 1, 0, 4 }) )
    private String summary;
    
    @SQLColumn(index = 5, name = "description", since = @SQLVersion({ 1, 0, 4 }) )
    private String description;

    public  Zone()
    {
        super();
    }

    public Zone(String name, String summary, String description)
    {
        super();
        this.id = UUID.randomUUID();
        this.name = name;
        this.summary = summary;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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
        Zone other = (Zone) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
}
