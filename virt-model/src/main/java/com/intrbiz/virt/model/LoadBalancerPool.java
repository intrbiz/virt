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
@JsonTypeName("load_balancer_pool")
@SQLTable(schema = VirtDB.class, name = "load_balancer_pool", since = @SQLVersion({ 1, 0, 21 }))
public class LoadBalancerPool
{    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("name")
    @SQLColumn(index = 2, name = "name", since = @SQLVersion({ 1, 0, 21 }))
    private String name;
    
    @JsonProperty("summary")
    @SQLColumn(index = 3, name = "summary", since = @SQLVersion({ 1, 0, 21 }))
    private String summary;
    
    @JsonProperty("description")
    @SQLColumn(index = 4, name = "description", since = @SQLVersion({ 1, 0, 21 }))
    private String description;
    
    @JsonIgnore
    @SQLColumn(index = 6, name = "network_id", notNull = true, since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = Network.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    private UUID networkId;

    @JsonProperty("endpoint")
    @SQLColumn(index = 7, name = "endpoint", notNull = true, since = @SQLVersion({ 1, 0, 21 }))
    @SQLUnique(name = "endpoint_unq")
    private String endpoint;

    public LoadBalancerPool()
    {
        super();
    }

    public LoadBalancerPool(Network network, String name, String summary, String description, String endpoint)
    {
        super();
        this.id = UUID.randomUUID();
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.networkId = network.getId();
        this.endpoint = endpoint;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public UUID getNetworkId()
    {
        return networkId;
    }

    public void setNetworkId(UUID networkId)
    {
        this.networkId = networkId;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    @JsonIgnore
    public int getTCPPortCount()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getLoadBalancerPoolTCPPortsForLoadBalancerPool(this.id).size();
        }
    }
    
    @JsonProperty("network")
    public Network getNetwork()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getNetwork(this.networkId);
        }
    }
}
