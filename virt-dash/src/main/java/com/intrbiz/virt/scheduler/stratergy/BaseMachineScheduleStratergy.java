package com.intrbiz.virt.scheduler.stratergy;

import java.util.List;
import java.util.stream.Collectors;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineInterfaceEO;
import com.intrbiz.virt.event.model.MachineVolumeEO;

public abstract class BaseMachineScheduleStratergy implements MachineScheduleStratergy
{   
    protected List<HostState> capableHostsForMachine(List<HostState> activeHostsInZone, MachineEO machine)
    {
        return activeHostsInZone.stream()
                .filter((host) -> this.isHostCapableForMachine(host, machine))
                .collect(Collectors.toList());
    }
    
    protected boolean isHostCapableForMachine(HostState host, MachineEO machine)
    {
        // does the machine support this type family
        if (! host.getSupportedMachineTypeFamilies().contains(machine.getMachineTypeFamily())) return false;
        // will this machine fit on the host
        if (host.getHostCPUs() < machine.getCpus() || host.getHostMemory() < machine.getMemory()) return false;
        // does the host support required networks
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            if (! host.getSupportedNetworkTypes().contains(nic.getNetwork().getType())) return false;
        }
        // does the host support the required volumes
        for (MachineVolumeEO vol : machine.getVolumes())
        {
            if (! host.getSupportedVolumeTypes().contains(vol.getType())) return false;
        }
        // does the host have enough undefined memory to support the machine
        long undefinedMemory = host.getHostMemory() - host.getDefinedMemory();
        if (undefinedMemory < machine.getMemory()) return false;
        // all looks good
        return true;
    }
}
