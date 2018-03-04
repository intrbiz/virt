package com.intrbiz.virt.manager.store;

import java.util.Set;

import com.intrbiz.virt.event.model.MachineVolumeEO;

public interface StoreManager
{
    void start();
    
    Set<String> getSupportedVolumeTypes();
    
    boolean isSupported(MachineVolumeEO vol);
    
    /**
     * Create a volume and return the path for it
     * @param vol the volume to create
     * @return the source path for the volume
     */
    String setupVolume(MachineVolumeEO vol);
}
