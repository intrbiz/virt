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
@JsonTypeName("load_balancer_backend_target")
@SQLTable(schema = VirtDB.class, name = "load_balancer_backend_target", since = @SQLVersion({ 1, 0, 27 }))
public class LoadBalancerBackendTarget
{       

    @JsonProperty("load_balancer_id")
    @SQLColumn(index = 1, name = "load_balancer_id", since = @SQLVersion({ 1, 0, 27 }))
    @SQLForeignKey(references = LoadBalancer.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 27 }))
    @SQLPrimaryKey()
    private UUID loadBalancerId;
    
    @JsonProperty("target")
    @SQLColumn(index = 2, name = "target", since = @SQLVersion({ 1, 0, 27 }))
    @SQLPrimaryKey()
    private String target;
    
    @JsonProperty("port")
    @SQLColumn(index = 3, name = "port", since = @SQLVersion({ 1, 0, 27 }))
    @SQLPrimaryKey()
    private int port;
    
    @JsonProperty("options")
    @SQLColumn(index = 5, name = "options", since = @SQLVersion({ 1, 0, 27 }))
    private String options;
    
    @JsonProperty("admin_state")
    @SQLColumn(index = 6, name = "admin_state", since = @SQLVersion({ 1, 0, 27 }))
    private AdminState adminState;

    public LoadBalancerBackendTarget()
    {
        super();
    }

    public LoadBalancerBackendTarget(LoadBalancer loadBalancer, String target, int port)
    {
        super();
        this.loadBalancerId = loadBalancer.getId();
        this.target = target;
        this.port = port;
        this.adminState = AdminState.ENABLED;
    }

    public UUID getLoadBalancerId()
    {
        return loadBalancerId;
    }

    public void setLoadBalancerId(UUID loadBalancerId)
    {
        this.loadBalancerId = loadBalancerId;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getOptions()
    {
        return options;
    }

    public void setOptions(String options)
    {
        this.options = options;
    }

    public AdminState getAdminState()
    {
        return adminState;
    }

    public void setAdminState(AdminState adminState)
    {
        this.adminState = adminState;
    }
}
