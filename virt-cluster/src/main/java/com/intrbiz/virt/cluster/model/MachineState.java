package com.intrbiz.virt.cluster.model;

import java.io.Serializable;
import java.util.UUID;

public class MachineState implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public static final String MANAGE_MACHINE_NAME_PREFIX = "m-";
    
    public static final class Capability {
        
        public static final String MACHINE = "machine";
        
        public static final String ROUTER = "router";
        
        public static final String VOLUME = "volume";
        
    }

    private UUID id;
    
    private String name;
    
    private MachineStatus status;
    
    private String host;
    
    private String hostName;
    
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
    
    public String getHostName()
    {
        return hostName;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
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

    public void updated()
    {
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public MachineState onHost(String host, String hostName)
    {
        this.host = host;
        this.hostName = hostName;
        return this;
    }
    
    public MachineState scheduled(String chosenHost, String hostName)
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
    
    public MachineState running(String host, String hostName)
    {
        this.host = host;
        this.status = MachineStatus.RUNNING;
        this.updated();
        return this;
    }
    
    public MachineState stopped(String host, String hostName)
    {
        this.host = null;
        this.status = MachineStatus.STOPPED;
        this.updated();
        return this;
    }
    
    public MachineState terminated(String host, String hostName)
    {
        this.host = null;
        this.status = MachineStatus.TERMINATED;
        this.updated();
        return this;
    }
    
    public MachineState failed(String host, String hostName)
    {
        this.host = null;
        this.status = MachineStatus.FAILED;
        this.updated();
        return this;
    }
}
