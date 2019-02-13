package com.intrbiz.virt.event.host;

import com.intrbiz.virt.event.model.PersistentVolumeEO;

public class ManageVolume extends VirtHostEvent
{
    private static final long serialVersionUID = 1L;
    
    public static enum Action {
        CREATE,
        DESTROY
    }

    private PersistentVolumeEO volume;
    
    private Action action;
    
    private boolean force;

    public ManageVolume()
    {
        super();
    }

    public ManageVolume(PersistentVolumeEO volume, Action action)
    {
        super();
        this.volume = volume;
        this.action = action;
        this.force = false;
    }
    
    public ManageVolume(PersistentVolumeEO volume, Action action, boolean force)
    {
        super();
        this.volume = volume;
        this.action = action;
        this.force = force;
    }

    public PersistentVolumeEO getVolume()
    {
        return volume;
    }

    public void setVolume(PersistentVolumeEO volume)
    {
        this.volume = volume;
    }

    public Action getAction()
    {
        return action;
    }

    public void setAction(Action action)
    {
        this.action = action;
    }

    public boolean isForce()
    {
        return force;
    }

    public void setForce(boolean force)
    {
        this.force = force;
    }

    public String toString()
    {
        return "ManageVolume[action=" + this.action + ", force=" + this.force + ", volume=" + this.volume + "]";
    }
}
