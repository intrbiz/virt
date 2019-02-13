package com.intrbiz.virt.cluster.model;

import java.io.Serializable;
import java.util.UUID;

public class RouterState implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String zoneId;

    private UUID accountId;

    private RouterStatus status;

    private String host;

    private long lastUpdated;

    public RouterState()
    {
        super();
    }

    public RouterState(String zoneId, UUID accountId)
    {
        super();
        this.zoneId = zoneId;
        this.accountId = accountId;
    }

    public String getZoneId()
    {
        return zoneId;
    }

    public void setZoneId(String zoneId)
    {
        this.zoneId = zoneId;
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public RouterStatus getStatus()
    {
        return status;
    }

    public void setStatus(RouterStatus status)
    {
        this.status = status;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public long getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }
    
    public static String getId(String zoneId, UUID accountId)
    {
        return zoneId + "." + accountId;
    }
    
    public String getId()
    {
        return getId(this.zoneId, this.accountId);
    }
}
