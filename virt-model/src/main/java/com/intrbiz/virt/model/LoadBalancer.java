package com.intrbiz.virt.model;

import java.sql.Timestamp;
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
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.util.NameUtil;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("load_balancer")
@SQLTable(schema = VirtDB.class, name = "load_balancer", since = @SQLVersion({ 1, 0, 21 }))
public class LoadBalancer
{    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("account_id")
    @SQLColumn(index = 2, name = "account_id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    private UUID accountId;
    
    @JsonProperty("name")
    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 21 }))
    @SQLUnique(name ="name_unq", columns = {"account_id", "name"})
    private String name;
    
    @JsonProperty("created")
    @SQLColumn(index = 4, name = "created", since = @SQLVersion({ 1, 0, 17 }))
    private Timestamp created;

    @JsonProperty("description")
    @SQLColumn(index = 5, name = "description", since = @SQLVersion({ 1, 0, 21 }))
    private String description;
    
    @JsonProperty("pool_id")
    @SQLColumn(index = 6, name = "pool_id", notNull = true, since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = LoadBalancerPool.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    private UUID poolId;
    
    @JsonProperty("mode")
    @SQLColumn(index = 7, name = "mode", since = @SQLVersion({ 1, 0, 21 }))
    private String mode;
    
    @JsonIgnore
    @SQLColumn(index = 8, name = "certificate_id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = ACMECertificate.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    private UUID certificateId;
    
    @JsonProperty("redirect_http")
    @SQLColumn(index = 9, name = "redirect_http", since = @SQLVersion({ 1, 0, 21 }))
    private boolean redirectHttp;
    
    @JsonIgnore
    @SQLColumn(index = 10, name = "tcp_port_id", since = @SQLVersion({ 1, 0, 21 }))
    @SQLForeignKey(references = LoadBalancerPoolTCPPort.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    private UUID tcpPortId;
    
    @JsonProperty("domains")
    @SQLColumn(index = 11, name = "domains", type="TEXT[]", since = @SQLVersion({ 1, 0, 21 }))
    private List<String> domains = new LinkedList<String>();
    
    @JsonProperty("health_check_interval")
    @SQLColumn(index = 12, name = "health_check_interval", since = @SQLVersion({ 1, 0, 21 }))
    private int healthCheckInterval;
    
    @JsonProperty("health_check_timeout")
    @SQLColumn(index = 13, name = "health_check_timeout", since = @SQLVersion({ 1, 0, 21 }))
    private int healthCheckTimeout;
    
    @JsonProperty("health_check_rise")
    @SQLColumn(index = 14, name = "health_check_rise", since = @SQLVersion({ 1, 0, 21 }))
    private int healthCheckRise;
    
    @JsonProperty("health_check_fall")
    @SQLColumn(index = 15, name = "health_check_fall", since = @SQLVersion({ 1, 0, 21 }))
    private int healthCheckFall;
    
    @JsonProperty("health_check_path")
    @SQLColumn(index = 16, name = "health_check_path", since = @SQLVersion({ 1, 0, 21 }))
    private String healthCheckPath;
    
    @JsonProperty("health_check_status")
    @SQLColumn(index = 17, name = "health_check_status", since = @SQLVersion({ 1, 0, 21 }))
    private String healthCheckStatus;
    
    @JsonProperty("health_check_mode")
    @SQLColumn(index = 18, name = "health_check_mode", since = @SQLVersion({ 1, 0, 21 }))
    private String healthCheckMode;
    
    @JsonIgnore
    @SQLColumn(index = 19, name = "generated_certificate_id", since = @SQLVersion({ 1, 0, 23 }))
    @SQLForeignKey(references = ACMECertificate.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 21 }))
    private UUID generatedCertificateId;

    public LoadBalancer()
    {
        super();
    }
    
    public LoadBalancer(Account account, LoadBalancerPool pool, String name, String mode, String description)
    {
        super();
        this.id = account.randomObjectId();
        this.accountId = account.getId();
        this.poolId = pool.getId();
        this.name = NameUtil.toSafeName(name);
        this.mode = mode;
        this.description = description;
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

    public Timestamp getCreated()
    {
        return created;
    }

    public void setCreated(Timestamp created)
    {
        this.created = created;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public UUID getPoolId()
    {
        return poolId;
    }

    public void setPoolId(UUID poolId)
    {
        this.poolId = poolId;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public UUID getCertificateId()
    {
        return certificateId;
    }

    public void setCertificateId(UUID certificateId)
    {
        this.certificateId = certificateId;
    }

    public boolean isRedirectHttp()
    {
        return redirectHttp;
    }

    public void setRedirectHttp(boolean redirectHttp)
    {
        this.redirectHttp = redirectHttp;
    }

    public UUID getTcpPortId()
    {
        return tcpPortId;
    }

    public void setTcpPortId(UUID tcpPortId)
    {
        this.tcpPortId = tcpPortId;
    }

    public List<String> getDomains()
    {
        return domains;
    }

    public void setDomains(List<String> domains)
    {
        this.domains = domains;
    }

    public int getHealthCheckInterval()
    {
        return healthCheckInterval;
    }

    public void setHealthCheckInterval(int healthCheckInterval)
    {
        this.healthCheckInterval = healthCheckInterval;
    }

    public int getHealthCheckTimeout()
    {
        return healthCheckTimeout;
    }

    public void setHealthCheckTimeout(int healthCheckTimeout)
    {
        this.healthCheckTimeout = healthCheckTimeout;
    }

    public int getHealthCheckRise()
    {
        return healthCheckRise;
    }

    public void setHealthCheckRise(int healthCheckRise)
    {
        this.healthCheckRise = healthCheckRise;
    }

    public int getHealthCheckFall()
    {
        return healthCheckFall;
    }

    public void setHealthCheckFall(int healthCheckFall)
    {
        this.healthCheckFall = healthCheckFall;
    }

    public String getHealthCheckPath()
    {
        return healthCheckPath;
    }

    public void setHealthCheckPath(String healthCheckPath)
    {
        this.healthCheckPath = healthCheckPath;
    }

    public String getHealthCheckStatus()
    {
        return healthCheckStatus;
    }

    public void setHealthCheckStatus(String healthCheckStatus)
    {
        this.healthCheckStatus = healthCheckStatus;
    }

    public String getHealthCheckMode()
    {
        return healthCheckMode;
    }

    public void setHealthCheckMode(String healthCheckMode)
    {
        this.healthCheckMode = healthCheckMode;
    }
    
    public UUID getGeneratedCertificateId()
    {
        return generatedCertificateId;
    }

    public void setGeneratedCertificateId(UUID generatedCertificateId)
    {
        this.generatedCertificateId = generatedCertificateId;
    }

    @JsonProperty("account")
    public Account getAccount()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getAccount(this.accountId);
        }
    }
    
    @JsonIgnore()
    public LoadBalancerPool getPool()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getLoadBalancerPool(this.poolId);
        }
    }
    
    @JsonProperty("certificate")
    public ACMECertificate getCertificate()
    {
        if (this.certificateId == null) return null;
        try (VirtDB db = VirtDB.connect())
        {
            return db.getACMECertificate(this.certificateId);
        }
    }
    
    @JsonProperty("generated_certificate")
    public ACMECertificate getGeneratedCertificate()
    {
        if (this.generatedCertificateId == null) return null;
        try (VirtDB db = VirtDB.connect())
        {
            return db.getACMECertificate(this.generatedCertificateId);
        }
    }
    
    @JsonProperty("tcp_port")
    public LoadBalancerPoolTCPPort getTcpPort()
    {
        if (this.tcpPortId == null) return null;
        try (VirtDB db = VirtDB.connect())
        {
            return db.getLoadBalancerPoolTCPPort(this.tcpPortId);
        }
    }
    
    @JsonProperty("backend_servers")
    public List<LoadBalancerBackendServer> getBackendServers()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getLoadBalancerBackendServersForLoadBalancer(this.id);
        }
    }
    
    @JsonProperty("backend_targets")
    public List<LoadBalancerBackendTarget> getBackendTargets()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getLoadBalancerBackendTargetsForLoadBalancer(this.id);
        }
    }
}
