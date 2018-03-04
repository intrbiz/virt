package com.intrbiz.virt.event.host;

import java.util.UUID;

public class StartMachine extends VirtHostEvent
{
    private static final long serialVersionUID = 1L;

    private UUID machineId;

    public StartMachine()
    {
        super();
    }

    public StartMachine(UUID machineId)
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
