package com.intrbiz.virt.cluster.model.health;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineHealthMemory implements Serializable
{
    private static final long serialVersionUID = 1L;

    @JsonProperty("total")
    private long total;
    
    @JsonProperty("available")
    private long available;
    
    @JsonProperty("percent")
    private float percent;
    
    @JsonProperty("used")
    private long used;
    
    @JsonProperty("free")
    private long free;

    @Override
    public String toString()
    {
        return "GuestLoad [total=" + total + ", available=" + available + ", percent=" + percent + ", used=" + used + ", free=" + free + "]";
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public long getAvailable()
    {
        return available;
    }

    public void setAvailable(long available)
    {
        this.available = available;
    }

    public float getPercent()
    {
        return percent;
    }

    public void setPercent(float percent)
    {
        this.percent = percent;
    }

    public long getUsed()
    {
        return used;
    }

    public void setUsed(long used)
    {
        this.used = used;
    }

    public long getFree()
    {
        return free;
    }

    public void setFree(long free)
    {
        this.free = free;
    }
}
