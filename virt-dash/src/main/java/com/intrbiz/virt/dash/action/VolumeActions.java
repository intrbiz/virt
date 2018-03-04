package com.intrbiz.virt.dash.action;

import static com.intrbiz.balsa.BalsaContext.*;

import org.apache.log4j.Logger;

import com.intrbiz.metadata.Action;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.model.PersistentVolume;

public class VolumeActions
{
    private static final Logger logger = Logger.getLogger(VolumeActions.class);
    
    private <T extends ClusterComponent> T getClusterComponent(Class<T> componentClass)
    {
        return ((App) Balsa().app()).getClusterManager().getComponent(componentClass);
    }
    
    @Action("volume.create")
    public void createVolume(PersistentVolume volume)
    {
    }
    
    @Action("volume.terminate")
    public void terminateVolume(PersistentVolume volume)
    {
    }
}
