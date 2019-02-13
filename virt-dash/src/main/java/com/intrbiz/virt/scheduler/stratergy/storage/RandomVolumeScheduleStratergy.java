package com.intrbiz.virt.scheduler.stratergy.storage;

import java.security.SecureRandom;
import java.util.List;

import org.apache.log4j.Logger;

import com.intrbiz.virt.cluster.model.HostState;
import com.intrbiz.virt.event.model.PersistentVolumeEO;

public class RandomVolumeScheduleStratergy extends BaseVolumeScheduleStratergy
{
    private static final Logger logger = Logger.getLogger(RandomVolumeScheduleStratergy.class);
    
    private final SecureRandom random = new SecureRandom();
    
    @Override
    public HostState scheduleVolume(List<HostState> activeHostsInZone, PersistentVolumeEO volume)
    {
        logger.info("Placing volume " + volume + " amongst " + activeHostsInZone);
        List<HostState> capableHosts = this.capableHostsForVolume(activeHostsInZone, volume);
        logger.debug("Capable hosts: " + capableHosts.size() + " " + capableHosts);
        if (capableHosts.isEmpty()) return null;
        // choose a host at random
        int choice = this.random.nextInt() % capableHosts.size();
        return capableHosts.get(choice);
    }
}
