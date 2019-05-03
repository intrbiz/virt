package com.intrbiz.virt.manager.store;

import static com.intrbiz.system.exec.Command.*;
import static com.intrbiz.virt.manager.store.StoreManager.TYPES.*;

import org.apache.log4j.Logger;

import com.intrbiz.system.exec.Command;
import com.intrbiz.system.exec.Result;
import com.intrbiz.system.exec.SystemExecutionException;
import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.config.StoreManagerCfg;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.event.model.PersistentVolumeEO;
import com.intrbiz.virt.event.model.MachineVolumeEO.VolumeMode;
import com.intrbiz.virt.manager.store.model.CephVolumeInfo;
import com.intrbiz.virt.manager.store.model.VolumeInfo;

public class CephStoreManager extends LibvirtEphemeralStoreManager
{   
    private static final String RBD = "/usr/bin/rbd";
    
    private static final String CREATE = "create";
    
    private static final String CLONE = "clone";
    
    private static final String INFO = "info";
    
    private static final String REMOVE = "rm";
    
    private static final String SIZE = "--size";
    
    private static final String OBJECT_SIZE = "--object-size";
    
    private static final String MEGABYTES = "M";
    
    private static final int[] RBD_SUCCESS_CODES = { 0 };
    
    private static final Logger logger = Logger.getLogger(CephStoreManager.class);
    
    private static final int MIN_OBJECT_SIZE = 4; // 4KiB
    
    private static final int MAX_OBJECT_SIZE = 131_072; // 128MiB
    
    private static final int DEFAULT_OBJECT_SIZE = 4_096; // 4MiB
    
    protected String cephHosts;
    
    protected String cephAuth;
    
    protected String cephMachinePool;
    
    protected String cephPersistentPool;
    
    public CephStoreManager()
    {
        super();
        this.registerType(CEPH);
    }
    
    @Override
    public void configure(StoreManagerCfg cfg) throws Exception
    {
        super.configure(cfg);
        this.cephHosts = cfg.getStringParameterValue("ceph.monitor.hosts", "172.26.29.115");
        this.cephMachinePool = cfg.getStringParameterValue("ceph.machine.pool.name", "machine");
        this.cephPersistentPool = cfg.getStringParameterValue("ceph.persistent.pool.name", "persistent");
        this.cephAuth = cfg.getStringParameterValue("ceph.auth.uuid", "6f3128fa-fada-463a-989f-b965c83e5da9");
    }
    
    @Override
    public VolumeInfo createOrAttachVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                return this.createCephVolume(vol);
        }
        return super.createOrAttachVolume(vol);
    }
    
    @Override
    public void releaseVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                this.releaseCephVolume(vol);
                return;
        }
        super.releaseVolume(vol);
    }
    
    @Override
    public void removeVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                this.removeCephVolume(vol);
                return;
        }
        super.removeVolume(vol);
    }
    
    @Override
    public void createPersistentVolume(PersistentVolumeEO pvol)
    {
        switch (pvol.getType())
        {
            case CEPH:
                this.createPersistentCephVolume(pvol);
                return;
        }
        super.createPersistentVolume(pvol);
    }
    
    @Override
    public void destroyPersistentVolume(PersistentVolumeEO pvol)
    {
        switch (pvol.getType())
        {
            case CEPH:
                this.destroyPersistentCephVolume(pvol);
                return;
        }
        super.destroyPersistentVolume(pvol);
    }
    
    private void releaseCephVolume(MachineVolumeEO vol)
    {
        // Nothing to do to release a Ceph volume
    }
    
    /**
     * Enforce that the given object size is within our limits, returning 
     * the default object size if not.
     */
    private int clampObjectSize(int size)
    {
        return size >= MIN_OBJECT_SIZE && size <= MAX_OBJECT_SIZE ? size : DEFAULT_OBJECT_SIZE;
    }
    
    private void createPersistentCephVolume(PersistentVolumeEO pvol)
    {
        // Create a persistent Ceph volume
        String source = this.prependPersistentPool(pvol.getSource());
        if (cephVolumeExists(source))
        {
            try
            {
                int objectSize = clampObjectSize(pvol.getObjectSize());
                logger.info("Creating ceph volumne " + source + " size " + pvol.getSize() + " object size " + objectSize);
                this.executor.expect(command(RBD, CREATE, OBJECT_SIZE, objectSize + "K", SIZE,  (pvol.getSize() / 1_000_000) + MEGABYTES, source), RBD_SUCCESS_CODES);
            }
            catch (SystemExecutionException e) 
            {
                throw new VirtError("Failed to create persistent ceph volume: " + source, e);
            }
        }
    }
    
    private void destroyPersistentCephVolume(PersistentVolumeEO pvol)
    {
        // Remove a persistent Ceph volume
        String source = this.prependPersistentPool(pvol.getSource());
        if (cephVolumeExists(source))
        {
            try
            {
                this.executor.expect(command(RBD, REMOVE, source), RBD_SUCCESS_CODES);
            }
            catch (SystemExecutionException e) 
            {
                throw new VirtError("Failed to destroy persistent ceph volume: " + source, e);
            }
        }
    }

    private CephVolumeInfo createCephVolume(MachineVolumeEO vol)
    {
        String source = null;
        Command command = null;
        switch (vol.getMode())
        {
            case CLONE:
                // Clone a machine volume
                source = this.prependMachinePool(vol.getSource());
                if (! cephVolumeExists(source))
                {
                    if (vol.getSourceParent() == null) throw new VirtError("Cannot clone from null source parent");
                    String parent = this.prependMachinePool(vol.getSourceParent());
                    if (! cephVolumeExists(parent)) throw new VirtError("Cannot clone, no such volume: " + parent);
                    int objectSize = DEFAULT_OBJECT_SIZE;
                    logger.info("Cloning ceph volumne " + source + " from snapshot " + parent + " object size " + objectSize);
                    command = command(RBD, CLONE, OBJECT_SIZE, objectSize + "K", parent, source);
                }
                break;
            case CREATE:
                // Create a machine volume
                source = this.prependMachinePool(vol.getSource());
                if (! cephVolumeExists(source))
                {
                    int objectSize = DEFAULT_OBJECT_SIZE;
                    logger.info("Creating ceph volumne " + source + " size " + vol.getSize() + " object size " + objectSize);
                    command = command(RBD, CREATE, OBJECT_SIZE, objectSize + "K", SIZE,  (vol.getSize() / 1_000_000) + MEGABYTES, source);
                }
                break;
            case ATTACH:
                // Attach to a persistent volume
                source = this.prependPersistentPool(vol.getSource());
                if (! cephVolumeExists(source)) throw new VirtError("Cannot attach, no such volume: " + source);
                logger.info("Attaching ceph volume " + source);
                break;
            default:
                throw new VirtError("Unsupported volume mode: " + vol.getMode());
        }
        if (command != null)
        {
            try
            {
                this.executor.expect(command, RBD_SUCCESS_CODES);
            }
            catch (SystemExecutionException e) 
            {
                throw new VirtError("Failed to setup " + vol.getMode() + " volume: " + source, e);
            }
        }
        return new CephVolumeInfo(vol.getSize(), this.cephHosts, this.cephAuth, source);
    }
    
    private void removeCephVolume(MachineVolumeEO vol)
    {
        // Remove Ceph volume which are non-persistent
        if (vol.getMode() == VolumeMode.CLONE || vol.getMode() == VolumeMode.CREATE)
        {
            String source = this.prependMachinePool(vol.getSource());
            if (cephVolumeExists(source))
            {
                try
                {
                    this.executor.expect(command(RBD, REMOVE, source), RBD_SUCCESS_CODES);
                }
                catch (SystemExecutionException e) 
                {
                    throw new VirtError("Failed to remove ceph volume: " + source, e);
                }
            }
        }
    }
    
    protected String prependMachinePool(String source)
    {
        return this.cephMachinePool + "/" + source;
    }
    
    protected String prependPersistentPool(String source)
    {
        return this.cephPersistentPool + "/" + source;
    }
    
    protected boolean cephVolumeExists(String imageName)
    {
        Result res = this.executor.exec(command(RBD, INFO, imageName));
        return res.isSuccess(RBD_SUCCESS_CODES);
    }
}
