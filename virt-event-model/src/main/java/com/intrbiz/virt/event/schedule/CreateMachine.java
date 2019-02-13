package com.intrbiz.virt.event.schedule;

import com.intrbiz.virt.event.model.MachineEO;

public class CreateMachine extends VirtScheduleEvent
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
