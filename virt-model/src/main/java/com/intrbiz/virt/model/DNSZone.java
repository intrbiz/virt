package com.intrbiz.virt.model;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("dns.zone")
@SQLTable(schema = VirtDB.class, name = "dns_zone", since = @SQLVersion({ 1, 0, 16 }))
public class DNSZone
{    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 16 }))
    @SQLPrimaryKey()
    private UUID id;

    @JsonProperty("zone_name")
    @SQLColumn(index = 2, name = "zone_name", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    @SQLUnique(name = "zone_name_unq")
    private String zoneName;
    
    @JsonProperty("owning_account_id")
    @SQLColumn(index = 3, name = "owning_account_id", notNull = true, since = @SQLVersion({ 1, 0, 16 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 16 }))
    private UUID owningAccountId;
    
    @JsonProperty("created")
    @SQLColumn(index = 4, name = "created", since = @SQLVersion({ 1, 0, 16 }))
    private Timestamp created;

    @JsonProperty("active")
    @SQLColumn(index = 5, name = "active", since = @SQLVersion({ 1, 0, 16 }))
    private boolean active = false;
    
    @JsonProperty("aliases")
    @SQLColumn(index = 6, name = "aliases", type="TEXT[]", since = @SQLVersion({ 1, 0, 16 }))
    private List<String> aliases = new LinkedList<String>();

    public DNSZone()
    {
        super();
    }
    
    public DNSZone(String zoneName, List<String> aliases, Account account)
    {
        super();
        this.id = account.randomObjectId();
        this.zoneName = qualifyZoneName(zoneName);
        this.owningAccountId = account.getId();
        this.created = new Timestamp(System.currentTimeMillis());
        this.active = true;
        if (aliases != null)
        {
            for (String alias : aliases)
            {
                this.aliases.add(qualifyZoneName(alias));
            }
        }
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getOwningAccountId()
    {
        return owningAccountId;
    }

    public void setOwningAccountId(UUID owningAccountId)
    {
        this.owningAccountId = owningAccountId;
    }

    public String getZoneName()
    {
        return zoneName;
    }

    public void setZoneName(String zoneName)
    {
        this.zoneName = zoneName;
    }

    public Timestamp getCreated()
    {
        return created;
    }

    public void setCreated(Timestamp created)
    {
        this.created = created;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public void setAliases(List<String> aliases)
    {
        this.aliases = aliases;
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
        DNSZone other = (DNSZone) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
    
    public static String qualifyZoneName(String zoneName)
    {
        zoneName = zoneName.toLowerCase();
        if (! zoneName.endsWith("."))
        {
            zoneName = zoneName + ".";
        }
        return zoneName;
    }
    
    public static boolean isFullyQualified(String name)
    {
        // Is the given name already fully qualified
        return name.endsWith(".");
    }
    
    public static String qualifyName(String name, String zoneName)
    {
        name = name.toLowerCase();
        // Is the given name already fully qualified
        if (isFullyQualified(name))
        {
            return name;
        }
        // Is the given name the alias for the zone name
        if ("@".equals(name))
        {
            return zoneName;
        }
        // Otherwise append the zone name
        return name + "." + zoneName;
    }
    
    public String qualifyNamePrimary(String name)
    {
        return qualifyName(name, this.zoneName);
    }
    
    public List<String> qualifyName(String name)
    {
        List<String> names = new LinkedList<String>();
        if (isFullyQualified(name))
        {
            names.add(name);
        }
        else
        {
            names.add(qualifyName(name, this.zoneName));
            for (String alias : this.aliases)
            {
                names.add(qualifyName(name, alias));    
            }
        }
        return names;
    }
    
    public static String prettyZoneName(String zoneName)
    {
        return zoneName.substring(0, zoneName.length() - 1);
    }
    
    public String getName()
    {
        return prettyZoneName(this.zoneName);
    }
    
    public String getNames()
    {
        if (this.aliases.isEmpty())
            return this.getName();
        return this.getName() + ", " + this.aliases.stream().map(DNSZone::prettyZoneName).collect(Collectors.joining(", "));
    }
    
    public Account getOwningAccount()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getAccount(this.owningAccountId);
        }
    }
    
    public List<DNSZoneRecord> getRecords()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getDNSZoneRecordsForZone(this.id);
        }
    }
}
