package com.intrbiz.virt.event.schedule;

import com.intrbiz.virt.event.model.PersistentVolumeEO;

public class CreateVolume extends VirtScheduleEvent
{
    private static final long serialVersionUID = 1L;

    private PersistentVolumeEO volume;

    public CreateVolume()
    {
        super();
    }

    public CreateVolume(PersistentVolumeEO volume)
    {
        super();
        this.volume = volume;
    }

    public PersistentVolumeEO getVolume()
    {
        return volume;
    }

    public void setVolume(PersistentVolumeEO volume)
    {
        this.volume = volume;
    }

    public String toString()
    {
        return "CreateVolume[volume=" + this.volume + "]";
    }
}
