package com.intrbiz.virt.model;

import java.security.KeyPair;
import java.sql.Timestamp;
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
import com.intrbiz.virt.util.ACMEUtil;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("acme_account")
@SQLTable(schema = VirtDB.class, name = "acme_account", since = @SQLVersion({ 1, 0, 17 }))
public class ACMEAccount
{    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 17 }))
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("account_id")
    @SQLColumn(index = 2, name = "account_id", since = @SQLVersion({ 1, 0, 17 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 17 }))
    private UUID accountId;
    
    @JsonProperty("created")
    @SQLColumn(index = 3, name = "created", since = @SQLVersion({ 1, 0, 17 }))
    private Timestamp created;

    @JsonProperty("key_pair")
    @SQLColumn(index = 4, name = "key_pair", notNull = true, since = @SQLVersion({ 1, 0, 17 }))
    private String keyPair;
    
    @JsonProperty("location")
    @SQLColumn(index = 4, name = "location", since = @SQLVersion({ 1, 0, 17 }))
    private String location;

    public ACMEAccount()
    {
        super();
    }
    
    public ACMEAccount(UUID accountId, KeyPair keyPair)
    {
        super();
        if (accountId != null)
        {
            this.id = Account.randomId(accountId);
            this.accountId = accountId;
        }
        else
        {
            this.id = Account.NULL_UUID;
            this.accountId = null;
        }
        this.created = new Timestamp(System.currentTimeMillis());
        this.keyPair = ACMEUtil.keyPairToString(keyPair);
    }
    
    public ACMEAccount(KeyPair keyPair)
    {
        this(null, keyPair);
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

    public Timestamp getCreated()
    {
        return created;
    }

    public void setCreated(Timestamp created)
    {
        this.created = created;
    }

    public String getKeyPair()
    {
        return keyPair;
    }

    public void setKeyPair(String keyPair)
    {
        this.keyPair = keyPair;
    }
    
    public KeyPair loadKeyPair()
    {
        return ACMEUtil.keyPairFromString(this.keyPair);
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}
