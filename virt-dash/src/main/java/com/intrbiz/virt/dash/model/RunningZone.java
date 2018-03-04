package com.intrbiz.virt.dash.model;

import com.intrbiz.virt.model.Zone;
import com.intrbiz.virt.scheduler.model.ZoneSchedulerState;

public class RunningZone
{
    private final Zone zone;
    
    private final ZoneSchedulerState schedulerState;

    public RunningZone(Zone zone, ZoneSchedulerState schedulerState)
    {
        super();
        this.zone = zone;
        this.schedulerState = schedulerState;
    }

    public Zone getZone()
    {
        return zone;
    }

    public ZoneSchedulerState getSchedulerState()
    {
        return schedulerState;
    }
}
