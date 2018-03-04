package com.intrbiz.virt.event.host;

import com.intrbiz.virt.event.model.MachineEO;

public class DestroyMachine extends VirtHostEvent
{
    private static final long serialVersionUID = 1L;

    private MachineEO machine;

    public DestroyMachine()
    {
        super();
    }

    public DestroyMachine(MachineEO machine)
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
        return "DestroyMachine[machine=" + this.machine + "]";
    }
}
