package com.intrbiz.virt.scheduler.stratergy;

import java.util.List;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.event.model.MachineEO;

public interface MachineScheduleStratergy
{
    HostState scheduleMachine(List<HostState> activeHostsInZone, MachineEO machine);
}
