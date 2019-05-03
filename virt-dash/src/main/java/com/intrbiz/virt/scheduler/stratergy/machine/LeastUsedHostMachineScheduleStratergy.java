package com.intrbiz.virt.scheduler.stratergy.machine;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.event.model.MachineEO;

public class LeastUsedHostMachineScheduleStratergy extends BaseMachineScheduleStratergy
{
    private static final Logger logger = Logger.getLogger(LeastUsedHostMachineScheduleStratergy.class);
    
    @Override
    public HostState scheduleMachine(List<HostState> activeHostsInZone, MachineEO machine)
    {
        logger.info("Placing machine " + machine + " amongst " + activeHostsInZone.stream().map(h -> h.getName()).collect(Collectors.toSet()));
        List<HostState> capableHosts = this.capableHostsForMachine(activeHostsInZone, machine);
        logger.info("Capable hosts: " + capableHosts.size() + " " + capableHosts);
        HostState leastUsed = null;
        for (HostState host : capableHosts)
        {
            if (leastUsed == null || computeHostWeight(host) < computeHostWeight(leastUsed))
                leastUsed = host;
        }
        return leastUsed;
    }
    
    private double computeHostWeight(HostState host)
    {
        return (host.getDefinedMachines() * 4.0D) + ((host.getHugepages2MiBTotal() - host.getHugepages2MiBFree()) / 512.0D);
    }
}
