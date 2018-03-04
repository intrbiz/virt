package com.intrbiz.virt.cluster.model.health;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineHealthCPU implements Serializable
{
    private static final long serialVersionUID = 1L;

    @JsonProperty("user")
    private float user;
    
    @JsonProperty("system")
    private float system;
    
    @JsonProperty("idle")
    private float idle;
    
    @JsonProperty("iowait")
    private float iowait;

    public float getUser()
    {
        return user;
    }

    public void setUser(float user)
    {
        this.user = user;
    }

    public float getSystem()
    {
        return system;
    }

    public void setSystem(float system)
    {
        this.system = system;
    }

    public float getIdle()
    {
        return idle;
    }

    public void setIdle(float idle)
    {
        this.idle = idle;
    }

    public float getIowait()
    {
        return iowait;
    }

    public void setIowait(float iowait)
    {
        this.iowait = iowait;
    }

    @Override
    public String toString()
    {
        return "GuestCPU [user=" + user + ", system=" + system + ", idle=" + idle + ", iowait=" + iowait + "]";
    }
}
