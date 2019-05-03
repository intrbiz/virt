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
@JsonTypeName("acme_well_known")
@SQLTable(schema = VirtDB.class, name = "acme_well_known", since = @SQLVersion({ 1, 0, 28 }))
public class ACMEWellKnown
{    
    @JsonProperty("host")
    @SQLColumn(index = 1, name = "host", notNull = true, since = @SQLVersion({ 1, 0, 28 }))
    @SQLPrimaryKey()
    private String host;
    
    @JsonProperty("name")
    @SQLPrimaryKey()
    @SQLColumn(index = 2, name = "name", since = @SQLVersion({ 1, 0, 28 }))
    private String name;
    
    @JsonProperty("challenge")
    @SQLColumn(index = 3, name = "challenge", since = @SQLVersion({ 1, 0, 28 }))
    private String challenge;

    public ACMEWellKnown()
    {
        super();
    }
    
    public ACMEWellKnown(String host, String name, String challenge)
    {
        super();
        this.host = host;
        this.name = name;
        this.challenge = challenge;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getChallenge()
    {
        return challenge;
    }

    public void setChallenge(String challenge)
    {
        this.challenge = challenge;
    }
}
