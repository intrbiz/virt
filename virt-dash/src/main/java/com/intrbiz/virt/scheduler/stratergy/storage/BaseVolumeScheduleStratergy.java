package com.intrbiz.virt.scheduler.stratergy.storage;

import java.util.List;
import java.util.stream.Collectors;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.cluster.model.MachineState.Capability;
import com.intrbiz.virt.event.model.PersistentVolumeEO;

public abstract class BaseVolumeScheduleStratergy implements VolumeScheduleStratergy
{   
    protected List<HostState> capableHostsForVolume(List<HostState> activeHostsInZone, PersistentVolumeEO volume)
    {
        return activeHostsInZone.stream()
                .filter((host) -> this.isHostCapableForVolume(host, volume))
                .collect(Collectors.toList());
    }
    
    protected boolean isHostCapableForVolume(HostState host, PersistentVolumeEO volume)
    {
        return host.hasCapability(Capability.VOLUME);
    }
}
