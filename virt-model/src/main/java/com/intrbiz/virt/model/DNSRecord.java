package com.intrbiz.virt.model;

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
@JsonTypeName("dns.record")
@SQLTable(schema = VirtDB.class, name = "dns_record", since = @SQLVersion({ 1, 0, 13 }))
public class DNSRecord implements DNSContent
{
    public static final class Scope
    {
        public static final String EXTERNAL = "external";
        
        public static final String INTERNAL = "internal";
    }
    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 13 }))
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("account_id")
    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 13 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID accountId;

    @JsonProperty("scope")
    @SQLColumn(index = 3, name = "scope", notNull = true, since = @SQLVersion({ 1, 0, 13 }))
    private String scope;
    
    @JsonProperty("type")
    @SQLColumn(index = 4, name = "type", notNull = true, since = @SQLVersion({ 1, 0, 13 }))
    private String type;

    @JsonProperty("name")
    @SQLColumn(index = 5, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 13 }))
    private String name;

    @JsonProperty("content")
    @SQLColumn(index = 6, name = "content", notNull = true, since = @SQLVersion({ 1, 0, 13 }))
    private String content;
    
    @JsonProperty("ttl")
    @SQLColumn(index = 7, name = "ttl", notNull = true, since = @SQLVersion({ 1, 0, 13 }))
    private int ttl;
    
    @JsonProperty("priority")
    @SQLColumn(index = 8, name = "priority", since = @SQLVersion({ 1, 0, 13 }))
    private int priority;
    
    @JsonProperty("alias")
    @SQLColumn(index = 9, name = "alias", since = @SQLVersion({ 1, 0, 23 }))
    private boolean alias;
    
    @JsonProperty("generated")
    @SQLColumn(index = 10, name = "generated", since = @SQLVersion({ 1, 0, 23 }))
    private boolean generated;

    public DNSRecord()
    {
        super();
    }
    
    public DNSRecord(UUID accountId, String scope, String type, String name, String content, int ttl, int priority, boolean alias, boolean generated)
    {
        super();
        this.id = Account.randomId(accountId);
        this.accountId = accountId;
        this.scope = scope;
        this.type = type;
        this.name = name;
        this.content = content;
        this.ttl = ttl;
        this.priority = priority;
        this.alias = alias;
        this.generated = generated;
    }
    
    public DNSRecord(Account account, String scope, String type, String name, String content, int ttl, int priority, boolean alias, boolean generated)
    {
        this(account.getId(), scope, type, name, content, ttl, priority, alias, generated);
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

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
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
        return hostedDomainName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DNSRecord other = (DNSRecord) obj;
        if (accountId == null)
        {
            if (other.accountId != null) return false;
        }
        else if (!accountId.equals(other.accountId)) return false;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        if (scope == null)
        {
            if (other.scope != null) return false;
        }
        else if (!scope.equals(other.scope)) return false;
        if (type == null)
        {
            if (other.type != null) return false;
        }
        else if (!type.equals(other.type)) return false;
        return true;
    }
}
