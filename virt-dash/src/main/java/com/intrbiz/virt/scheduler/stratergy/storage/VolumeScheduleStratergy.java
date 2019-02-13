package com.intrbiz.virt.scheduler.stratergy.storage;

import java.util.List;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.event.model.PersistentVolumeEO;

public interface VolumeScheduleStratergy
{
    HostState scheduleVolume(List<HostState> activeHostsInZone, PersistentVolumeEO volume);
}
