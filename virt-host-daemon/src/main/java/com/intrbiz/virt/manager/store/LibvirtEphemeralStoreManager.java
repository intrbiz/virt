package com.intrbiz.virt.manager.store;

import static com.intrbiz.virt.manager.store.StoreManager.TYPES.*;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.intrbiz.virt.config.StoreManagerCfg;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.libvirt.CloseListener;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.manager.HostManagerContext;
import com.intrbiz.virt.manager.HostMetadataStoreContext;
import com.intrbiz.virt.manager.store.model.FileVolumeInfo;
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
    public VolumeInfo createVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case EPHEMERAL:
                return this.setupEphemeralVolume(vol);
        }
        return super.createVolume(vol);
    }
    
    @Override
    public void releaseVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case EPHEMERAL:
                this.releaseEphemeralVolume(vol);
                break;
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
                break;
        }
        super.removeVolume(vol);
    }
    
    private FileVolumeInfo setupEphemeralVolume(MachineVolumeEO vol)
    {
        // TODO
        return null;
    }
    
    private void removeEphemeralVolume(MachineVolumeEO vol)
    {
        // TODO
    }
    
    private void releaseEphemeralVolume(MachineVolumeEO vol)
    {
        // TODO
    }
    
}
