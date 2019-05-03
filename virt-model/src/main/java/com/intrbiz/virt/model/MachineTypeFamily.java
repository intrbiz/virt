package com.intrbiz.virt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("machine.type.family")
@SQLTable(schema = VirtDB.class, name = "machine_type_family", since = @SQLVersion({ 1, 0, 32 }))
public class MachineTypeFamily
{
    @JsonProperty("family")
    @SQLColumn(index = 1, name = "family", notNull = true, since = @SQLVersion({ 1, 0, 32 }))
    @SQLPrimaryKey()
    private String family;
    
    @JsonProperty("summary")
    @SQLColumn(index = 2, name = "summary", since = @SQLVersion({ 1, 0, 32 }))
    private String summary;
    
    @JsonProperty("description")
    @SQLColumn(index = 3, name = "description", since = @SQLVersion({ 1, 0, 32 }))
    private String description;

    public MachineTypeFamily()
    {
        super();
    }

    public MachineTypeFamily(String family)
    {
        super();
        this.family = family;
    }

    public String getFamily()
    {
        return family;
    }

    public void setFamily(String family)
    {
        this.family = family;
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
}
