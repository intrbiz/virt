package com.intrbiz.virt.event.model;

public enum MachineAdminStatus
{
    RUNNING(true),
    STOPPED(true),
    PENDING(false),
    TERMINATED(false);
    
    private final boolean allocated;
    
    private MachineAdminStatus(boolean allocated)
    {
        this.allocated = allocated;
    }
    
    public boolean isAllocated()
    {
        return this.allocated;
    }
    
    public boolean isRunning()
    {
        return this == RUNNING;
    }
    
    public boolean isStopped()
    {
        return this == STOPPED;
    }
}
