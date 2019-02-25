package com.intrbiz.virt.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.util.NameUtil;

@SQLTable(schema = VirtDB.class, name = "account", since = @SQLVersion({ 1, 0, 0 }))
public class Account implements Serializable
{
    private static final long serialVersionUID = 1L;

    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @SQLColumn(index = 2, name = "name", since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "account_name_unq")
    private String name;

    @SQLColumn(index = 3, name = "summary", since = @SQLVersion({ 1, 0, 0 }))
    private String summary;

    @SQLColumn(index = 4, name = "created", since = @SQLVersion({ 1, 0, 0 }))
    private Timestamp created;

    @SQLColumn(index = 5, name = "active", since = @SQLVersion({ 1, 0, 0 }))
    private boolean active = false;

    public Account()
    {
        super();
    }

    public Account(String summary)
    {
        super();
        this.id = UUID.randomUUID();
        this.summary = summary;
        this.name = NameUtil.toSafeName(summary);
        this.created = new Timestamp(System.currentTimeMillis());
        this.active = false;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
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

    public Timestamp getCreated()
    {
        return created;
    }

    public void setCreated(Timestamp created)
    {
        this.created = created;
    }
    
    public List<User> getOwners()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getUserForAccountWithRole(this.id, Role.ACCOUNT_OWNER.ordinal());
        }
    }
    
    public List<User> getAdmins()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getUserForAccountWithRole(this.id, Role.ACCOUNT_ADMIN.ordinal());
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
        Account other = (Account) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
}
