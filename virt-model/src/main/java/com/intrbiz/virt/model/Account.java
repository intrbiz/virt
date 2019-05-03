package com.intrbiz.virt.model;

import java.io.Serializable;
import java.sql.Timestamp;
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
import com.intrbiz.virt.event.model.AccountEO;
import com.intrbiz.virt.util.NameUtil;


@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("account")
@SQLTable(schema = VirtDB.class, name = "account", since = @SQLVersion({ 1, 0, 0 }))
public class Account implements Serializable
{
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @JsonProperty("name")
    @SQLColumn(index = 2, name = "name", since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "account_name_unq")
    private String name;

    @JsonProperty("summary")
    @SQLColumn(index = 3, name = "summary", since = @SQLVersion({ 1, 0, 0 }))
    private String summary;

    @JsonProperty("created")
    @SQLColumn(index = 4, name = "created", since = @SQLVersion({ 1, 0, 0 }))
    private Timestamp created;

    @JsonProperty("active")
    @SQLColumn(index = 5, name = "active", since = @SQLVersion({ 1, 0, 0 }))
    private boolean active = false;

    public Account()
    {
        super();
    }

    public Account(String name, String summary)
    {
        super();
        this.id = randomAccountId();
        this.name = NameUtil.toSafeName(name);
        this.summary = summary;
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
    
    @JsonIgnore
    public List<User> getOwners()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getUserForAccountWithRole(this.id, Role.ACCOUNT_OWNER.ordinal());
        }
    }
    
    @JsonIgnore
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
    
    // Id Handling
    
    /**
     * Generate a random object Id for this account
     * @return
     */
    public UUID randomObjectId()
    {
        return randomId(this.id);
    }
    
    /**
     * Is the given object Id part of this account
     * @param objectId
     * @return
     */
    public boolean isOurs(UUID objectId)
    {
        return isObjectOfAccount(this.id, objectId);
    }
    
    /**
     * Generate a new random account id, account ids only use the upper 48 bits.
     * 
     * @return the site id
     */
    public static UUID randomAccountId()
    {
        return getAccountId(UUID.randomUUID());
    }
    
    public static final UUID NULL_UUID = new UUID(0, 0);

    /**
     * Generate a random object id within the given account
     * 
     * @param siteId
     * @return
     */
    public static UUID randomId(UUID accountId)
    {
        return setAccountId(accountId, UUID.randomUUID());
    }

    /**
     * Get the account id for the given object id
     * 
     * @param objectId
     * @return
     */
    public static UUID getAccountId(UUID objectId)
    {
        return new UUID((objectId.getMostSignificantBits() & 0xFFFFFFFF_FFFF0000L) | 0x0000000000004000L, 0x80000000_00000000L);
    }

    /**
     * Set the account id into the given object id
     * 
     * @param siteId
     * @param objectId
     * @return
     */
    public static UUID setAccountId(UUID accountId, UUID objectId)
    {
        return new UUID((accountId.getMostSignificantBits() & 0xFFFFFFFF_FFFF0000L) | (objectId.getMostSignificantBits() & 0x00000000_0000FFFFL), objectId.getLeastSignificantBits());
    }
    
    /**
     * Is the given object id from the given account id
     * @param accountId
     * @param objectId
     * @return
     */
    public static boolean isObjectOfAccount(UUID accountId, UUID objectId)
    {
        return (accountId.getMostSignificantBits() & 0xFFFFFFFF_FFFF0000L) == (objectId.getMostSignificantBits() & 0xFFFFFFFF_FFFF0000L);
    }
    
    public AccountEO toEvent()
    {
        return new AccountEO(this.id, this.name);
    }
}
