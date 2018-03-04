package com.intrbiz.virt.cluster.model;

import java.io.Serializable;
import java.util.UUID;

public class MachineState implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public static final String MANAGE_MACHINE_NAME_PREFIX = "m-";

    private UUID id;
    
    private String name;
    
    private MachineStatus status;
    
    private String host;
    
    private boolean autostart;
    
    private boolean persistent;
    
    private long lastUpdated;
    
    public MachineState()
    {
        super();
    }
    
    public MachineState(UUID id)
    {
        super();
        this.id = id;
        this.name = (MANAGE_MACHINE_NAME_PREFIX + id).toLowerCase();
        this.status = MachineStatus.DEFINED;
        this.host = null;
        this.autostart = false;
        this.persistent = false;
        this.lastUpdated = System.currentTimeMillis();
    }

    public MachineState(UUID id, String name, MachineStatus status, boolean autostart, boolean persistent)
    {
        super();
        this.id = id;
        this.name = name;
        this.status = status;
        this.autostart = autostart;
        this.persistent = persistent;
        this.lastUpdated = System.currentTimeMillis();
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
    
    public boolean isUnmanaged()
    {
        return ! (MANAGE_MACHINE_NAME_PREFIX + this.id).toLowerCase().equals(this.name);
    }

    public MachineStatus getStatus()
    {
        return status;
    }

    public void setStatus(MachineStatus status)
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

    public boolean isAutostart()
    {
        return autostart;
    }

    public void setAutostart(boolean autostart)
    {
        this.autostart = autostart;
    }

    public boolean isPersistent()
    {
        return persistent;
    }

    public void setPersistent(boolean persistent)
    {
        this.persistent = persistent;
    }

    public long getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }
    
    // Helpers
    
    public void updated()
    {
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public MachineState scheduled(String chosenHost)
    {
        this.host = chosenHost;
        this.status = MachineStatus.SCHEDULED;
        this.updated();
        return this;
    }
    
    public MachineState pending()
    {
        this.host = null;
        this.status = MachineStatus.PENDING;
        this.updated();
        return this;
    }
    
    public MachineState running(String host)
    {
        this.host = host;
        this.status = MachineStatus.RUNNING;
        this.updated();
        return this;
    }
    
    public MachineState stopped(String host)
    {
        this.host = null;
        this.status = MachineStatus.STOPPED;
        this.updated();
        return this;
    }
    
    public MachineState terminated(String host)
    {
        this.host = null;
        this.status = MachineStatus.TERMINATED;
        this.updated();
        return this;
    }
    
    public MachineState failed(String host)
    {
        this.host = null;
        this.status = MachineStatus.FAILED;
        this.updated();
        return this;
    }
}
