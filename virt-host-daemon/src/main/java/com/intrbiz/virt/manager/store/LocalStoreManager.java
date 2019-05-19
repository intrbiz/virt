package com.intrbiz.virt.manager.store;

import static com.intrbiz.system.exec.Command.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.intrbiz.system.exec.SystemExecutionException;
import com.intrbiz.system.exec.SystemExecutorService;
import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.config.StoreManagerCfg;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.event.model.PersistentVolumeEO;
import com.intrbiz.virt.event.model.MachineVolumeEO.VolumeMode;
import com.intrbiz.virt.manager.store.model.FileVolumeInfo;
import com.intrbiz.virt.manager.store.model.FileVolumeInfo.Format;
import com.intrbiz.virt.manager.store.model.VolumeInfo;

import static com.intrbiz.virt.manager.store.StoreManager.TYPES.*;

public class LocalStoreManager extends BaseStoreManager
{
    
    private static final String QEMU_IMG = "/usr/bin/qemu-img";
    
    private static final String CREATE = "create";
    
    private static final String QCOW2 = "qcow2";
    
    private static final String FORMAT = "-f";
    
    private static final String BACKING_FORMAT = "-F";
    
    private static final String BACKING = "-b";
    
    private static final int[] QEMU_IMG_SUCCESS_CODES = { 0 };
    
    private static final Logger logger = Logger.getLogger(LocalStoreManager.class);
    
    protected final SystemExecutorService executor = SystemExecutorService.getSystemExecutorService();
    
    protected File localDir;
    
    public LocalStoreManager()
    {
        super();
        this.registerType(LOCAL);
        this.registerType(EPHEMERAL);
    }
    
    @Override
    public void configure(StoreManagerCfg cfg) throws Exception
    {
        super.configure(cfg);
        this.localDir = new File(cfg.getStringParameterValue("local.file.directory", "/srv/vms"));
    }

    @Override
    public VolumeInfo createOrAttachVolume(String machineFamily, MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case LOCAL:
                return this.setupLocalVolume(vol);
        }
        return super.createOrAttachVolume(machineFamily, vol);
    }
    
    @Override
    public void releaseVolume(String machineFamily, MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case LOCAL:
                this.releaseLocalVolume(vol);
                return;
        }
        super.releaseVolume(machineFamily, vol);
    }
    
    @Override
    public void removeVolume(String machineFamily, MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case LOCAL:
                this.removeLocalVolume(vol);
                return;
        }
        super.removeVolume(machineFamily, vol);
    }
    
    @Override
    public void createPersistentVolume(PersistentVolumeEO pvol)
    {
        switch (pvol.getType())
        {
            case LOCAL:
                this.createPersistentLocalVolume(pvol);
                return;
        }
        super.createPersistentVolume(pvol);
    }
    
    @Override
    public void destroyPersistentVolume(PersistentVolumeEO pvol)
    {
        switch (pvol.getType())
        {
            case LOCAL:
                this.destroyPersistentLocalVolume(pvol);
                return;
        }
        super.destroyPersistentVolume(pvol);
    }
    
    private void createPersistentLocalVolume(PersistentVolumeEO pvol)
    {
        File source = new File(this.localDir, pvol.getSource() + "." + QCOW2);
        if (! source.exists())
        {
            logger.info("Creating new local qcow2 volumne " + source.getAbsolutePath());
            source.getParentFile().mkdirs();
            List<String> args = Arrays.asList(CREATE, FORMAT, QCOW2, source.getAbsolutePath(), String.valueOf(pvol.getSize()));
            try
            {
                this.executor.expect(command(QEMU_IMG, args), QEMU_IMG_SUCCESS_CODES);
            }
            catch (SystemExecutionException e) 
            {
                throw new VirtError("Failed to create persistent volume qcow2 image", e);
            }
        }
    }
    
    private void destroyPersistentLocalVolume(PersistentVolumeEO pvol)
    {
        File source = new File(this.localDir, pvol.getSource() + "." + QCOW2);
        if (source.exists())
        {
            source.delete();
        }
    }
    
    private FileVolumeInfo setupLocalVolume(MachineVolumeEO vol)
    {
        File source = new File(this.localDir, vol.getSource() + "." + QCOW2);
        if (! source.exists())
        {
            List<String> args = null;
            switch (vol.getMode())
            {
                case CLONE:
                    File parent = new File(this.localDir, vol.getSourceParent() + "." + QCOW2);
                    if (! parent.exists()) throw new VirtError("Cannot find parent volume: " + vol.getSourceParent());
                    logger.info("Creating local qcow2 volumne " + source.getAbsolutePath() + " cloned from " + parent.getAbsolutePath());
                    source.getParentFile().mkdirs();
                    args = Arrays.asList(CREATE, FORMAT, QCOW2, BACKING, parent.getAbsolutePath(), BACKING_FORMAT, QCOW2, source.getAbsolutePath());
                    break;
                case CREATE:
                    logger.info("Creating new local qcow2 volumne " + source.getAbsolutePath());
                    source.getParentFile().mkdirs();
                    args = Arrays.asList(CREATE, FORMAT, QCOW2, source.getAbsolutePath(), String.valueOf(vol.getSize()));
                    break;
                case ATTACH:
                    if (! source.exists()) throw new VirtError("Cannot attach, no such volume: " + source.getAbsolutePath());
                    logger.info("Attaching local volume " + source);
                    break;
                default:
                    throw new VirtError("Unsupported volume mode" + vol.getMode());
            }
            if (args != null)
            {
                try
                {
                    this.executor.expect(command(QEMU_IMG, args), QEMU_IMG_SUCCESS_CODES);
                }
                catch (SystemExecutionException e) 
                {
                    throw new VirtError("Failed to " + vol.getMode() + " qcow2 image", e);
                }
            }
        }
        return new FileVolumeInfo(vol.getSize(), Format.QCOW2, source.getAbsolutePath());
    }
    
    private void removeLocalVolume(MachineVolumeEO vol)
    {
        File source = new File(this.localDir, vol.getSource() + "." + QCOW2);
        if (source.exists() && (vol.getMode() == VolumeMode.CLONE || vol.getMode() == VolumeMode.CREATE))
        {
            source.delete();
        }
    }
    
    private void releaseLocalVolume(MachineVolumeEO vol)
    {
        // Nothing to do
    }
}
