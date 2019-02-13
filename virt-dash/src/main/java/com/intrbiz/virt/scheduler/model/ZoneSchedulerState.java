package com.intrbiz.virt.scheduler.model;

import java.io.Serializable;

import com.hazelcast.core.Member;
import com.intrbiz.Util;

public class ZoneSchedulerState implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;

    private boolean running;

    private String currentOwnerId;
    
    private String currentOwnerName;

    public ZoneSchedulerState()
    {
        super();
    }

    public ZoneSchedulerState(String id, boolean running, Member member, String hostName)
    {
        super();
        this.id = id;
        this.running = running;
        this.currentOwnerId = member.getUuid();
        this.currentOwnerName = Util.coalesceEmpty(hostName, member.getAddress().getHost());
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public String getCurrentOwnerId()
    {
        return currentOwnerId;
    }

    public void setCurrentOwnerId(String currentOwnerId)
    {
        this.currentOwnerId = currentOwnerId;
    }

    public String getCurrentOwnerName()
    {
        return currentOwnerName;
    }

    public void setCurrentOwnerName(String currentOwnerName)
    {
        this.currentOwnerName = currentOwnerName;
    }
}
