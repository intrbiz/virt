package com.intrbiz.virt.dash.action;

import org.apache.log4j.Logger;

import com.intrbiz.metadata.Action;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.event.schedule.CreateVolume;
import com.intrbiz.virt.model.PersistentVolume;

public class VolumeActions extends ClusteredAction
{
    private static final Logger logger = Logger.getLogger(VolumeActions.class);
    
    @Action("volume.create")
    public void createVolume(PersistentVolume volume)
    {
        // get the scheduler event manager
        SchedulerEventManager<VirtDashCfg> schedulerEvents = this.getSchedulerEventManager();
        // send the schedule machine event
        logger.info("Creating volume: " + volume.getId() + " " + volume.getName());
        schedulerEvents.sendZoneSchedulerEvent(volume.getZone().getName(), new CreateVolume(volume.toEvent()));
    }
    
    @Action("volume.destroy")
    public void destroyVolume(PersistentVolume volume)
    {
    }
}
