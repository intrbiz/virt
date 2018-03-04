package com.intrbiz.virt.event.host;

import java.util.UUID;

public class RebootMachine extends VirtHostEvent
{
    private static final long serialVersionUID = 1L;

    private UUID machineId;
    
    private boolean force;

    public RebootMachine()
    {
        super();
    }

    public RebootMachine(UUID machineId, boolean force)
    {
        super();
        this.machineId = machineId;
        this.force = force;
    }

    public UUID getMachineId()
    {
        return machineId;
    }

    public void setMachineId(UUID machineId)
    {
        this.machineId = machineId;
    }

    public boolean isForce()
    {
        return force;
    }

    public void setForce(boolean force)
    {
        this.force = force;
    }

    public String toString()
    {
        return "RebootMachine[machine=" + this.machineId + ", force=" + this.force + "]";
    }
}
