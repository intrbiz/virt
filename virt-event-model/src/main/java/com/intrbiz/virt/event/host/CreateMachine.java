package com.intrbiz.virt.event.host;

import com.intrbiz.virt.event.model.MachineEO;

public class CreateMachine extends VirtHostEvent
{
    private static final long serialVersionUID = 1L;

    private MachineEO machine;

    public CreateMachine()
    {
        super();
    }

    public CreateMachine(MachineEO machine)
    {
        super();
        this.machine = machine;
    }

    public MachineEO getMachine()
    {
        return machine;
    }

    public void setMachine(MachineEO machine)
    {
        this.machine = machine;
    }

    public String toString()
    {
        return "CreateMachine[machine=" + this.machine + "]";
    }
}
