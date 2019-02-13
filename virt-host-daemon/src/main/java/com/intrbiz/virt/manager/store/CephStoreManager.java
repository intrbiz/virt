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
    
    private static final String SIZE = "-s";
    
    private static final String MEGABYTES = "M";
    
    private static final int[] RBD_SUCCESS_CODES = { 0 };
    
    private static final Logger logger = Logger.getLogger(CephStoreManager.class);
    
    protected String cephHosts;
    
    protected String cephAuth;
    
    protected String cephMachinePool;
    
    public CephStoreManager()
    {
        super();
        this.registerType(CEPH);
    }
    
    @Override
    public void configure(StoreManagerCfg cfg) throws Exception
    {
        super.configure(cfg);
        this.cephHosts = cfg.getStringParameterValue("ceph.monitor.hosts", "172.26.30.31,172.26.30.32,172.26.30.33,172.26.30.3,172.26.30.34,172.26.30.35,172.26.30.36,172.26.30.37");
        this.cephMachinePool = cfg.getStringParameterValue("ceph.machine.pool.name", "important");
        this.cephAuth = cfg.getStringParameterValue("ceph.auth.uuid", "6f3128fa-fada-463a-989f-b965c83e5da9");
    }
    
    @Override
    public VolumeInfo createVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                return this.setupCephVolume(vol);
        }
        return super.createVolume(vol);
    }
    
    @Override
    public void releaseVolume(MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                this.releaseCephVolume(vol);
                break;
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
                break;
        }
        super.removeVolume(vol);
    }
    
    private void releaseCephVolume(MachineVolumeEO vol)
    {
        // Nothing to do to release a Ceph volume
    }
    
    private void removeCephVolume(MachineVolumeEO vol)
    {
        // Remove Ceph volume which are non-persistent
        String source = this.prependMachinePool(vol.getSource());
        if (cephVolumeExists(vol.getSource()) && (vol.getMode() == VolumeMode.CLONE || vol.getMode() == VolumeMode.CREATE))
        {
            try
            {
                this.executor.expect(command(RBD, REMOVE, source), RBD_SUCCESS_CODES);
            }
            catch (SystemExecutionException e) 
            {
                throw new VirtError("Failed to remove ceph volume", e);
            }
        }
    }

    private CephVolumeInfo setupCephVolume(MachineVolumeEO vol)
    {
        String source = this.prependMachinePool(vol.getSource());
        if (! cephVolumeExists(vol.getSource()))
        {
            Command command = null;
            switch (vol.getMode())
            {
                case CLONE:
                    if (vol.getSourceParent() == null) throw new VirtError("Cannot clone from null source parent");
                    String parent = this.prependMachinePool(vol.getSourceParent());
                    if (! cephVolumeExists(parent)) throw new VirtError("Cannot clone, no such volume: " + parent);
                    logger.info("Cloning ceph volumne " + source + " from snapshot " + parent);
                    command = command(RBD, CLONE, parent, source);
                    break;
                case CREATE:
                    logger.info("Creating ceph volumne " + source + " size " + vol.getSize());
                    command = command(RBD, CREATE, SIZE,  (vol.getSize() / 1_000_000) + MEGABYTES, source);
                    break;
                case ATTACH:
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
                    throw new VirtError("Failed to setup " + vol.getMode() + " volume", e);
                }
            }
        }
        return new CephVolumeInfo(vol.getSize(), this.cephHosts, this.cephAuth, source);
    }
    
    protected String prependMachinePool(String source)
    {
        return this.cephMachinePool + "/" + source;
    }
    
    protected boolean cephVolumeExists(String imageName)
    {
        Result res = this.executor.exec(command(RBD, INFO, imageName));
        return res.isSuccess(RBD_SUCCESS_CODES);
    }
}
