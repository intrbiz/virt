package com.intrbiz.virt.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("zone")
@SQLTable(schema = VirtDB.class, name = "zone", since = @SQLVersion({ 1, 0, 4 }) )
public class Zone
{
    @JsonIgnore
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 4 }) )
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("name")
    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 4 }) )
    @SQLUnique(name = "name_unq")
    private String name;
    
    @JsonProperty("summary")
    @SQLColumn(index = 4, name = "summary", notNull = true, since = @SQLVersion({ 1, 0, 4 }) )
    private String summary;
    
    @JsonProperty("description")
    @SQLColumn(index = 5, name = "description", since = @SQLVersion({ 1, 0, 4 }) )
    private String description;
    
    @JsonProperty("placement_groups")
    @SQLColumn(index = 5, name = "placement_groups", type = "TEXT[]", since = @SQLVersion({ 1, 0, 28 }))
    private List<String> placementGroups = new LinkedList<String>();

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
        this.placementGroups = new LinkedList<String>(Arrays.asList("a", "b", "c"));
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

    public List<String> getPlacementGroups()
    {
        return placementGroups;
    }

    public void setPlacementGroups(List<String> placementGroups)
    {
        this.placementGroups = placementGroups;
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
