package com.intrbiz.virt.cluster.model.health;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineHealth implements Serializable
{
    private static final long serialVersionUID = 1L;

    @JsonProperty("gathered_at")
    private long gatheredAt;
    
    @JsonProperty("cpu")
    private MachineHealthCPU cpu;
    
    @JsonProperty("memory")
    private MachineHealthMemory memory;
    
    @JsonProperty("load")
    private MachineHealthLoad load;

    public long getGatheredAt()
    {
        return gatheredAt;
    }

    public void setGatheredAt(long gatheredAt)
    {
        this.gatheredAt = gatheredAt;
    }

    public MachineHealthCPU getCpu()
    {
        return cpu;
    }

    public void setCpu(MachineHealthCPU cpu)
    {
        this.cpu = cpu;
    }

    public MachineHealthMemory getMemory()
    {
        return memory;
    }

    public void setMemory(MachineHealthMemory memory)
    {
        this.memory = memory;
    }

    public MachineHealthLoad getLoad()
    {
        return load;
    }

    public void setLoad(MachineHealthLoad load)
    {
        this.load = load;
    }

    @Override
    public String toString()
    {
        return "GuestHealth [gatheredAt=" + gatheredAt + ", cpu=" + cpu + ", memory=" + memory + ", load=" + load + "]";
    }
}
