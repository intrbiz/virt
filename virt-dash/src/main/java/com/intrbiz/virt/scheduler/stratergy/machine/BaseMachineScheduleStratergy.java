package com.intrbiz.virt.scheduler.stratergy.machine;

import java.util.List;
import java.util.stream.Collectors;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.cluster.model.MachineState.Capability;
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
        if (! host.hasCapability(Capability.MACHINE)) return false;
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
        // does the host have enough free hugepages to support the machine
        long neededHugesPages = machine.getMemory() / (2L * 1024L * 1024L);
        if (host.getHugepages2MiBFree() < neededHugesPages) return false;
        // apply placement rule
        if (! checkPlacement(host, machine))
            return false;
        // all looks good
        return true;
    }
    
    protected boolean checkPlacement(HostState host, MachineEO machine)
    {
        if (machine.getPlacementRule() == null || "any".equals(machine.getPlacementRule()))
            return true;
        if (machine.getPlacementRule().equals(host.getPlacementGroup()))
            return true;
        if (machine.getPlacementRule().startsWith("!") && (! machine.getPlacementRule().substring(1).equals(host.getPlacementGroup())))
                return true;
        return false;
    }
}
