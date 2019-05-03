package com.intrbiz.virt.scheduler.stratergy.machine;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.event.model.MachineEO;

public class RandomMachineScheduleStratergy extends BaseMachineScheduleStratergy
{
    private static final Logger logger = Logger.getLogger(RandomMachineScheduleStratergy.class);
    
    private final SecureRandom random = new SecureRandom();
    
    @Override
    public HostState scheduleMachine(List<HostState> activeHostsInZone, MachineEO machine)
    {
        logger.info("Placing machine " + machine + " amongst " + activeHostsInZone.stream().map(h -> h.getName()).collect(Collectors.toSet()));
        List<HostState> capableHosts = this.capableHostsForMachine(activeHostsInZone, machine);
        logger.info("Capable hosts: " + capableHosts.size() + " " + capableHosts);
        if (capableHosts.isEmpty()) return null;
        // choose a host at random
        int choice = Math.abs(this.random.nextInt()) % capableHosts.size();
        return capableHosts.get(choice);
    }
}
