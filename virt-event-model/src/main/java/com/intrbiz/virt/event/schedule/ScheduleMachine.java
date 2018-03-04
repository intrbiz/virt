package com.intrbiz.virt.event.schedule;

import com.intrbiz.virt.event.model.MachineEO;

public class ScheduleMachine extends VirtScheduleEvent
{
    private static final long serialVersionUID = 1L;

    private MachineEO machine;

    public ScheduleMachine()
    {
        super();
    }

    public ScheduleMachine(MachineEO machine)
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
