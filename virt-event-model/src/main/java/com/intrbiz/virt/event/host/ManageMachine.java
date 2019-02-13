package com.intrbiz.virt.event.host;

import com.intrbiz.virt.event.model.MachineEO;

public class ManageMachine extends VirtHostEvent
{
    private static final long serialVersionUID = 1L;
    
    public static enum Action {
        CREATE,
        START,
        STOP,
        REBOOT,
        RELEASE,
        TERMINATE
    }

    private MachineEO machine;
    
    private Action action;
    
    private boolean force;

    public ManageMachine()
    {
        super();
    }

    public ManageMachine(MachineEO machine, Action action)
    {
        super();
        this.machine = machine;
        this.action = action;
        this.force = false;
    }
    
    public ManageMachine(MachineEO machine, Action action, boolean force)
    {
        super();
        this.machine = machine;
        this.action = action;
        this.force = force;
    }

    public MachineEO getMachine()
    {
        return machine;
    }

    public void setMachine(MachineEO machine)
    {
        this.machine = machine;
    }

    public Action getAction()
    {
        return action;
    }

    public void setAction(Action action)
    {
        this.action = action;
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
        return "ManageMachine[action=" + this.action + ", force=" + this.force + ", machine=" + this.machine + "]";
    }
}
