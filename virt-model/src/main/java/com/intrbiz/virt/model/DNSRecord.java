package com.intrbiz.virt.model;

import java.util.UUID;

import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@SQLTable(schema = VirtDB.class, name = "dns_record", since = @SQLVersion({ 1, 0, 12 }))
public class DNSRecord
{
    public static final class Scope
    {
        public static final String PUBLIC = "public";
        
        public static final String INTERNAL = "internal";
    }
    
    @SQLColumn(index = 1, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 12 }))
    @SQLPrimaryKey()
    private UUID accountId;

    @SQLColumn(index = 2, name = "scope", notNull = true, since = @SQLVersion({ 1, 0, 12 }))
    @SQLPrimaryKey()
    private String scope;
    
    @SQLColumn(index = 3, name = "type", notNull = true, since = @SQLVersion({ 1, 0, 12 }))
    @SQLPrimaryKey()
    private String type;

    @SQLColumn(index = 4, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 12 }))
    @SQLPrimaryKey()
    private String name;

    @SQLColumn(index = 5, name = "content", notNull = true, since = @SQLVersion({ 1, 0, 12 }))
    private String content;
    
    @SQLColumn(index = 6, name = "ttl", notNull = true, since = @SQLVersion({ 1, 0, 12 }))
    private int ttl;
    
    @SQLColumn(index = 7, name = "priority", since = @SQLVersion({ 1, 0, 12 }))
    private int priority;

    public DNSRecord()
    {
        super();
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
