package com.intrbiz.virt.model;

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
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("image")
@SQLTable(schema = VirtDB.class, name = "image", since = @SQLVersion({ 1, 0, 0 }))
public class Image
{
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @JsonProperty("name")
    @SQLColumn(index = 2, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private String name;

    @JsonProperty("size")
    @SQLColumn(index = 3, name = "size", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private long size;

    @JsonIgnore
    @SQLColumn(index = 4, name = "source", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "source_unq")
    private String source;

    @JsonProperty("account_id")
    @SQLColumn(index = 5, name = "account_id", notNull = false, since = @SQLVersion({ 1, 0, 0 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }))
    private UUID accountId;

    @JsonProperty("open")
    @SQLColumn(index = 6, name = "open", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private boolean open = false;
    
    @JsonProperty("description")
    @SQLColumn(index = 7, name = "description", since = @SQLVersion({ 1, 0, 0 }))
    private String description;
    
    @JsonProperty("provider")
    @SQLColumn(index = 8, name = "provider", since = @SQLVersion({ 1, 0, 0 }))
    private String provider;
    
    @JsonProperty("vendor")
    @SQLColumn(index = 9, name = "vendor", since = @SQLVersion({ 1, 0, 0 }))
    private String vendor;
    
    @JsonProperty("product")
    @SQLColumn(index = 10, name = "product", since = @SQLVersion({ 1, 0, 0 }))
    private String product;
    
    @JsonIgnore
    @SQLColumn(index = 11, name = "volume_type", since = @SQLVersion({ 1, 0, 0 }))
    private String volumeType;
    
    @JsonIgnore
    @SQLColumn(index = 12, name = "metadata", type = "JSONB", since = @SQLVersion({ 1, 0, 0 }) )
    private String metadata;

    public Image()
    {
        super();
    }
    
    /**
     * Create a new public image
     */
    public Image(String name, long size, String volumeType, String source)
    {
        super();
        this.id = UUID.randomUUID();
        this.open = true;
        this.name = name;
        this.size = size;
        this.volumeType = volumeType;
        this.source = source;
    }
    
    /**
     * Create an account specific image
     */
    public Image(Account account, String name, long size, String volumeType, String source)
    {
        super();
        this.id = account.randomObjectId();
        this.accountId = account.getId();
        this.open = false;
        this.name = name;
        this.size = size;
        this.volumeType = volumeType;
        this.source = source;
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

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    /**
     * Get the storage source path for the root volume of this image
     */
    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public boolean isOpen()
    {
        return open;
    }

    public void setOpen(boolean open)
    {
        this.open = open;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }

    public String getProduct()
    {
        return product;
    }

    public void setProduct(String product)
    {
        this.product = product;
    }

    public String getVolumeType()
    {
        return volumeType;
    }

    public void setVolumeType(String volumeType)
    {
        this.volumeType = volumeType;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
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
        Image other = (Image) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
}
