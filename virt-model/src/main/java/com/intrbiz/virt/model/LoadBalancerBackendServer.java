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
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("load_balancer_backend_server")
@SQLTable(schema = VirtDB.class, name = "load_balancer_backend_server", since = @SQLVersion({ 1, 0, 21 }))
public class LoadBalancerBackendServer
{      
    
    @JsonProperty("load_balancer_id")
    @SQLColumn(index = 1, name = "load_balancer_id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = LoadBalancer.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    @SQLPrimaryKey()
    private UUID loadBalancerId;
    
    @JsonProperty("machine_id")
    @SQLColumn(index = 2, name = "machine_id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = Machine.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    @SQLPrimaryKey()
    private UUID machineId;
    
    @JsonProperty("port")
    @SQLColumn(index = 3, name = "port", since = @SQLVersion({ 1, 0, 21 }))
    @SQLPrimaryKey()
    private int port;
    
    @JsonProperty("options")
    @SQLColumn(index = 5, name = "options", since = @SQLVersion({ 1, 0, 21 }))
    private String options;
    
    @JsonProperty("admin_state")
    @SQLColumn(index = 6, name = "admin_state", since = @SQLVersion({ 1, 0, 21 }))
    private AdminState adminState;

    public LoadBalancerBackendServer()
    {
        super();
    }

    public LoadBalancerBackendServer(LoadBalancer loadBalancer, Machine machine, int port)
    {
        super();
        this.loadBalancerId = loadBalancer.getId();
        this.machineId = machine.getId();
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

    public UUID getMachineId()
    {
        return machineId;
    }

    public void setMachineId(UUID machineId)
    {
        this.machineId = machineId;
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

    @JsonIgnore
    public Machine getMachine()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachine(this.machineId);
        }
    }
    
    @JsonProperty("machine_nic")
    public MachineNIC getMachineNic()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getLoadBalancerBackendServerMachineNic(this.loadBalancerId, this.machineId);
        }
    }
}
