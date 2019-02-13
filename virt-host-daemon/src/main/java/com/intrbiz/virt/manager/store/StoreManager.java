package com.intrbiz.virt.manager.store;

import java.util.Set;

import com.intrbiz.virt.config.StoreManagerCfg;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.manager.Manager;
import com.intrbiz.virt.manager.store.model.VolumeInfo;

public interface StoreManager extends Manager<StoreManagerCfg>
{    
    Set<String> getSupportedVolumeTypes();
    
    boolean isSupported(MachineVolumeEO vol);
    
    /**
     * Create a volume and return the path for it
     * @param vol the volume to create
     * @return information about the created volume
     */
    VolumeInfo createVolume(MachineVolumeEO vol);
    
    /**
     * Release a volume from this host.
     * This will do nothing to shared volumes 
     * but would remove local volumes
     */
    void releaseVolume(MachineVolumeEO vol);
    
    /**
     * Remove a volume from this host.
     * This will remove non-persistent shared 
     * volumes and any local volumes.
     */
    void removeVolume(MachineVolumeEO vol);
    
    public static interface TYPES {
        
        public static final String LOCAL = "local";
        
        public static final String EPHEMERAL = "ephemeral";
        
        public static final String CEPH = "ceph";
    }
}