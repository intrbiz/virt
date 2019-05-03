package com.intrbiz.virt.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonTypeName("ssh.key.set")
@SQLTable(schema = VirtDB.class, name = "ssh_key", since = @SQLVersion({ 1, 0, 0 }))
public class SSHKey
{
    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @JsonProperty("account_id")
    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID accountId;

    @JsonProperty("name")
    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private String name;

    @JsonProperty("key")
    @SQLColumn(index = 4, name = "key", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private String key;
    
    @JsonProperty("additional_keys")
    @SQLColumn(index = 5, name = "additional", type ="TEXT[]", since = @SQLVersion({ 1, 0, 10 }))
    private List<String> additional = new LinkedList<String>();

    public SSHKey()
    {
        super();
    }

    public SSHKey(Account account, String name, String... keys)
    {
        super();
        this.id = account.randomObjectId();
        this.accountId = account.getId();
        this.name = name;
        if (keys.length > 0) this.key = keys[0];
        for (int i = 1; i < keys.length; i++)
        {
            this.additional.add(keys[i]);
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

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
    
    public List<String> getAdditional()
    {
        return additional;
    }

    public void setAdditional(List<String> additional)
    {
        this.additional = additional;
    }
    
    @JsonIgnore
    public List<String> getAllKeys()
    {
        List<String> all = new LinkedList<>();
        all.add(this.key);
        all.addAll(this.additional);
        return all;
    }
    
    public List<String> getAllKeysWrapped(int length)
    {
        List<String> all = new LinkedList<>();
        all.add(wrapKey(this.key, length));
        for (String key : this.additional)
        {
            all.add(wrapKey(key, length));
        }
        return all;
    }

    public static String wrapKey(String key, int length)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length(); i++)
        {
            if (i > 0 && (i % length) == 0)
                sb.append("\r\n");
            sb.append(key.charAt(i));
        }
        return sb.toString();
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
        SSHKey other = (SSHKey) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
}
