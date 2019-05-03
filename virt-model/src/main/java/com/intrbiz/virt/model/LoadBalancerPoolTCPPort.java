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
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("load_balancer_pool_tcp_port")
@SQLTable(schema = VirtDB.class, name = "load_balancer_pool_tcp_port", since = @SQLVersion({ 1, 0, 21 }))
public class LoadBalancerPoolTCPPort
{    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("pool_id")
    @SQLColumn(index = 2, name = "pool_id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = LoadBalancerPool.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    private UUID poolId;
    
    @JsonProperty("port")
    @SQLColumn(index = 4, name = "port", since = @SQLVersion({ 1, 0, 21 }))
    private int port;

    @JsonProperty("endpoint")
    @SQLColumn(index = 3, name = "endpoint", notNull = true, since = @SQLVersion({ 1, 0, 21 }))
    @SQLUnique(name = "endpoint_unq", columns = { "endpoint", "port" })
    private String endpoint;
    
    @JsonProperty("bind")
    @SQLColumn(index = 4, name = "bind", since = @SQLVersion({ 1, 0, 21 }))
    @SQLUnique(name = "bind_unq", columns = { "bind", "port" })
    private String bind;
    
    @JsonProperty("allocated")
    @SQLColumn(index = 5, name = "allocated", since = @SQLVersion({ 1, 0, 29 }))
    private boolean allocated = false;

    public LoadBalancerPoolTCPPort()
    {
        super();
    }

    public LoadBalancerPoolTCPPort(LoadBalancerPool pool, int port, String endpoint, String bind)
    {
        super();
        this.id = UUID.randomUUID();
        this.poolId = pool.getId();
        this.port = port;
        this.endpoint = endpoint;
        this.bind = bind;
        this.allocated = false;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getPoolId()
    {
        return poolId;
    }

    public void setPoolId(UUID poolId)
    {
        this.poolId = poolId;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public String getBind()
    {
        return bind;
    }

    public void setBind(String bind)
    {
        this.bind = bind;
    }

    public boolean isAllocated()
    {
        return allocated;
    }

    public void setAllocated(boolean allocated)
    {
        this.allocated = allocated;
    }
}
