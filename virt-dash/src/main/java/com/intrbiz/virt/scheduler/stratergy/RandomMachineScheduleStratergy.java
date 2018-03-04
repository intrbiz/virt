package com.intrbiz.virt.scheduler.stratergy;

import java.security.SecureRandom;
import java.util.List;

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
        logger.info("Placing machine " + machine + " amongst " + activeHostsInZone);
        List<HostState> capableHosts = this.capableHostsForMachine(activeHostsInZone, machine);
        logger.debug("Capable hosts: " + capableHosts.size() + " " + capableHosts);
        if (capableHosts.isEmpty()) return null;
        // choose a host at random
        int choice = this.random.nextInt() % capableHosts.size();
        return capableHosts.get(choice);
    }
}
