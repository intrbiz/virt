package com.intrbiz.virt.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("dns.zone_record")
@SQLTable(schema = VirtDB.class, name = "dns_zone_record", since = @SQLVersion({ 1, 0, 16 }))
public class DNSZoneRecord implements DNSContent
{    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 16 }))
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("zone_id")
    @SQLColumn(index = 2, name = "zone_id", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    @SQLForeignKey(references = DNSZone.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 16 }))
    private UUID zoneId;
    
    @JsonProperty("type")
    @SQLColumn(index = 4, name = "type", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    private String type;

    @JsonProperty("name")
    @SQLColumn(index = 5, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    private String name;
    
    @JsonProperty("qualified_names")
    @SQLColumn(index = 5, name = "qualified_names", type = "TEXT[]", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    private List<String> qualifiedNames = new LinkedList<String>();

    @JsonProperty("content")
    @SQLColumn(index = 6, name = "content", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    private String content;
    
    @JsonProperty("ttl")
    @SQLColumn(index = 7, name = "ttl", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    private int ttl;
    
    @JsonProperty("priority")
    @SQLColumn(index = 8, name = "priority", since = @SQLVersion({ 1, 0, 16 }))
    private int priority;
    
    @JsonProperty("alias")
    @SQLColumn(index = 9, name = "alias", since = @SQLVersion({ 1, 0, 20 }))
    private boolean alias;
    
    @JsonProperty("generated")
    @SQLColumn(index = 10, name = "generated", since = @SQLVersion({ 1, 0, 23 }))
    private boolean generated;

    public DNSZoneRecord()
    {
        super();
    }
    
    public DNSZoneRecord(DNSZone zone, String type, String name, String content, int ttl, int priority, boolean alias, boolean generated)
    {
        super();
        this.id = Account.randomId(zone.getId());
        this.zoneId = zone.getId();
        this.type = type;
        this.name = name;
        this.qualifiedNames = zone.qualifyName(name);
        this.content = content;
        this.ttl = ttl;
        this.priority = priority;
        this.alias = alias;
        this.generated = generated;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getZoneId()
    {
        return zoneId;
    }

    public void setZoneId(UUID zoneId)
    {
        this.zoneId = zoneId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getQualifiedNames()
    {
        return qualifiedNames;
    }

    public void setQualifiedNames(List<String> qualifiedNames)
    {
        this.qualifiedNames = qualifiedNames;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getTtl()
    {
        return ttl;
    }

    public void setTtl(int ttl)
    {
        this.ttl = ttl;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public boolean isAlias()
    {
        return alias;
    }

    public void setAlias(boolean alias)
    {
        this.alias = alias;
    }
    
    public boolean isGenerated()
    {
        return generated;
    }

    public void setGenerated(boolean generated)
    {
        this.generated = generated;
    }
    
    public String getZoneName(String hostedDomainName)
    {
        return this.getZone().getZoneName();
    }

    public DNSZone getZone()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getDNSZone(this.zoneId);
        }
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
        DNSZoneRecord other = (DNSZoneRecord) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
}
