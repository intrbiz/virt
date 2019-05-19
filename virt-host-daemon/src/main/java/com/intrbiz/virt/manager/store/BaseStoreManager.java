package com.intrbiz.virt.manager.store;

import java.util.Set;
import java.util.TreeSet;

import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.config.StoreManagerCfg;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.event.model.PersistentVolumeEO;
import com.intrbiz.virt.manager.HostManagerContext;
import com.intrbiz.virt.manager.HostMetadataStoreContext;
import com.intrbiz.virt.manager.store.model.VolumeInfo;

public abstract class BaseStoreManager implements StoreManager
{
    protected final Set<String> supportedTypes = new TreeSet<String>();

    protected StoreManagerCfg config;

    protected BaseStoreManager()
    {
        super();
    }

    @Override
    public void configure(StoreManagerCfg cfg) throws Exception
    {
        this.config = cfg;
    }

    @Override
    public StoreManagerCfg getConfiguration()
    {
        return this.config;
    }

    protected void registerType(String type)
    {
        this.supportedTypes.add(type);
    }

    public void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext)
    {
    }

    @Override
    public Set<String> getSupportedVolumeTypes()
    {
        return this.supportedTypes;
    }

    @Override
    public boolean isSupported(MachineVolumeEO vol)
    {
        return this.supportedTypes.contains(vol.getType());
    }

    @Override
    public VolumeInfo createOrAttachVolume(String machineFamily, MachineVolumeEO vol)
    {
        throw new VirtError("Volume type " + vol.getType() + " is not supported");
    }
    
    @Override
    public void releaseVolume(String machineFamily, MachineVolumeEO vol)
    {
        throw new VirtError("Volume type " + vol.getType() + " is not supported");
    }
    
    @Override
    public void removeVolume(String machineFamily, MachineVolumeEO vol)
    {
        throw new VirtError("Volume type " + vol.getType() + " is not supported");
    }
    
    @Override
    public void createPersistentVolume(PersistentVolumeEO pvol)
    {
        throw new VirtError("Persistent volumes are not supported");
    }
    
    @Override
    public void destroyPersistentVolume(PersistentVolumeEO pvol)
    {
        throw new VirtError("Persistent volumes are not supported");
    }
}
