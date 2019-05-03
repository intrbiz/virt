package com.intrbiz.virt.manager.store;

import static com.intrbiz.virt.manager.store.StoreManager.TYPES.*;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.config.StoreManagerCfg;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.libvirt.CloseListener;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.storage.LibVirtStorageVolumeDef;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStoragePool;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStorageVol;
import com.intrbiz.virt.manager.HostManagerContext;
import com.intrbiz.virt.manager.HostMetadataStoreContext;
import com.intrbiz.virt.manager.store.model.BlockVolumeInfo;
import com.intrbiz.virt.manager.store.model.VolumeInfo;

public class LibvirtEphemeralStoreManager extends LocalStoreManager
{       
    private static final Logger logger = Logger.getLogger(LibvirtEphemeralStoreManager.class);

    private String libvirtURL;

    private final Timer timer = new Timer();

    private LibVirtAdapter connection;
    
    public LibvirtEphemeralStoreManager()
    {
        super();
        this.registerType(EPHEMERAL);
    }
    
    @Override
    public void configure(StoreManagerCfg cfg) throws Exception
    {
        super.configure(cfg);
        this.libvirtURL = cfg.getStringParameterValue("libvirt.url", "qemu+tcp://root@127.0.0.1:16509/system");
    }

    @Override
    public void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext)
    {
        this.connect();
    }

    public String getLibvirtURL()
    {
        return this.libvirtURL;
    }

    protected LibVirtAdapter getConnection()
    {
        return connection;
    }

    /**
     * Connect to the LibVirt daemon on the host and do the initial setup
     */
    private void connect()
    {
        logger.trace("Connecting to " + this.libvirtURL);
        try
        {
            this.connection = LibVirtAdapter.connect(this.libvirtURL);
            // add a close listener to reconnect
            this.connection.addCloseListener(new CloseListener()
            {
                @Override
                public void onClose(LibVirtAdapter adapter)
                {
                    // schedule reconnect
                    logger.info("Scheduling reconnect to libvirt");
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            connect();
                        }
                    }, 1_000L);
                    connection = null;
                }
            });
        }
        catch (Exception e)
        {
            logger.warn("Error connecting to host", e);
            // schedule reconnect
            logger.info("Scheduling reconnect to libvirt");
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    connect();
                }
            }, 5_000L);
        }
    }

    protected boolean isConnected()
    {
        return this.connection != null && this.connection.isConnected() && this.connection.isAlive();
    }
    
    @Override
    public VolumeInfo createOrAttachVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case EPHEMERAL:
                return this.setupEphemeralVolume(vol);
        }
        return super.createOrAttachVolume(vol);
    }
    
    @Override
    public void releaseVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case EPHEMERAL:
                this.releaseEphemeralVolume(vol);
                return;
        }
        super.releaseVolume(vol);
    }
    
    @Override
    public void removeVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case EPHEMERAL:
                this.removeEphemeralVolume(vol);
                return;
        }
        super.removeVolume(vol);
    }
    
    private String getVolTypeMetadata(MachineVolumeEO vol, String key, String defaultValue)
    {
        if (vol == null || vol.getTypeMetadata() == null) return defaultValue;
        String value = vol.getTypeMetadata().get(key);
        return value == null ? defaultValue : value;
    }
    
    private BlockVolumeInfo setupEphemeralVolume(MachineVolumeEO vol)
    {
        if (!this.isConnected()) throw new VirtError("Cannot setup ephemeral volume at this time, please try again.");
        String poolName = this.getVolTypeMetadata(vol, "pool", "ephemeral");
        LibVirtStoragePool pool = this.connection.lookupStoragePoolByName(poolName);
        if (pool == null) throw new VirtError("Could not find storage pool " + poolName);
        // Create the volume
        LibVirtStorageVolumeDef volDef = LibVirtStorageVolumeDef.createFull(vol.getSource(), vol.getSize());
        logger.info("Creating ephemeral volume " + vol.getName() + " in pool " + pool.getName() + ": " + volDef);
        LibVirtStorageVol lvVol = pool.addStorageVol(volDef);
        String devicePath = lvVol.getStorageVolDef().getTarget().getPath();
        logger.info("Created ephemeral volume: " + devicePath);
        return new BlockVolumeInfo(vol.getSize(), devicePath);
    }
    
    private void removeEphemeralVolume(MachineVolumeEO vol)
    {
        if (!this.isConnected()) throw new VirtError("Cannot remove ephemeral volume at this time, please try again.");
        String poolName = this.getVolTypeMetadata(vol, "pool", "ephemeral");
        LibVirtStoragePool pool = this.connection.lookupStoragePoolByName(poolName);
        if (pool == null) throw new VirtError("Could not find storage pool " + poolName);
        // Get the volume
        logger.info("Looking for volume " + vol.getSource() + " in pool " + pool.getName());
        LibVirtStorageVol lvVol = pool.lookupStorageVolByName(vol.getSource());
        if (lvVol != null)
        {
            lvVol.delete();
            logger.info("Volume " + lvVol.getPath() + " deleted");
        }
    }
    
    private void releaseEphemeralVolume(MachineVolumeEO vol)
    {
        // Delete the volume on release
        this.removeEphemeralVolume(vol);
    }
    
}
