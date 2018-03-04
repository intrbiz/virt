package com.intrbiz.virt.event.host;

import java.util.UUID;

public class StopMachine extends VirtHostEvent
{
    private static final long serialVersionUID = 1L;

    private UUID machineId;

    public StopMachine()
    {
        super();
    }

    public StopMachine(UUID machineId)
    {
        super();
        this.machineId = machineId;
    }

    public UUID getMachineId()
    {
        return machineId;
    }

    public void setMachineId(UUID machineId)
    {
        this.machineId = machineId;
    }

    public String toString()
    {
        return "StopMachine[machine=" + this.machineId + "]";
    }
}
