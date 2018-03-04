package com.intrbiz.virt.cluster.model.health;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineHealthLoad implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("1min")
    private float min1;
    
    @JsonProperty("5min")
    private float min5;
    
    @JsonProperty("15min")
    private float min15;

    @Override
    public String toString()
    {
        return "GuestLoad [min1=" + min1 + ", min5=" + min5 + ", min15=" + min15 + "]";
    }

    public float getMin1()
    {
        return min1;
    }

    public void setMin1(float min1)
    {
        this.min1 = min1;
    }

    public float getMin5()
    {
        return min5;
    }

    public void setMin5(float min5)
    {
        this.min5 = min5;
    }

    public float getMin15()
    {
        return min15;
    }

    public void setMin15(float min15)
    {
        this.min15 = min15;
    }
}
