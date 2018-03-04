package com.intrbiz.virt.manager.store;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.util.ExecUtil;
import com.intrbiz.virt.util.ExecUtil.ExecResult;

public class DefaultStoreManager implements StoreManager
{   
    private final Logger logger = Logger.getLogger(DefaultStoreManager.class);
    
    private File localDir = new File("/data/vms");
    
    private String cephMachinePool = "important";
    
    public DefaultStoreManager()
    {
        super();
    }
    
    public void start()
    {
    }
    
    @Override
    public Set<String> getSupportedVolumeTypes()
    {
        return new TreeSet<String>(Arrays.asList("ceph", "local"));
    }
    
    @Override
    public boolean isSupported(MachineVolumeEO vol)
    {
        return "ceph".equals(vol.getType()) || "local".equals(vol.getType());
    }

    @Override
    public String setupVolume(MachineVolumeEO vol)
    {
        if ("ceph".equals(vol.getType()))
        {
            return this.setupCephVolume(vol);
        }
        else if ("local".equals(vol.getType()))
        {
            return this.setupLocalVolume(vol);
        }
        else
        {
            throw new VirtError("Volume type " + vol.getType() + " is not supported");
        }
    }
    
    protected String setupCephVolume(MachineVolumeEO vol)
    {
        String source = this.prependMachinePool(vol.getSource());
        if (! cephVolumeExists(vol.getSource()))
        {
            List<String> args = null;
            switch (vol.getMode())
            {
                case CLONE:
                    if (vol.getSourceParent() == null) throw new VirtError("Cannot clone from null source parent");
                    String parent = this.prependMachinePool(vol.getSourceParent());
                    if (! cephVolumeExists(parent)) throw new VirtError("Cannot clone, no such volume: " + parent);
                    logger.info("Cloning ceph volumne " + source + " from snapshot " + parent);
                    args = Arrays.asList("clone", parent, source);
                    break;
                case CREATE:
                    logger.info("Creating ceph volumne " + source + " size " + vol.getSize());
                    args = Arrays.asList("create", "-s",  (vol.getSize() / 1_000_000) + "M", source);
                    break;
                case ATTACH:
                    if (! cephVolumeExists(source)) throw new VirtError("Cannot attach, no such volume: " + source);
                    logger.info("Attaching ceph volume " + source);
                    break;
                default:
                    throw new VirtError("Unsupported volume mode" + vol.getMode());
            }
            if (args != null)
            {
                ExecResult res = ExecUtil.exec("/usr/bin/rbd", args);
                if (res.exit != 0) throw new VirtError("Failed to setup ceph volume: " + res.exit + "\n" + res.out + "\n" + res.error);
            }
        }
        return source;
    }
    
    protected String prependMachinePool(String source)
    {
        return this.cephMachinePool + "/" + source;
    }
    
    protected boolean cephVolumeExists(String imageName)
    {
        ExecResult res = ExecUtil.exec("/usr/bin/rbd", Arrays.asList("info", imageName));
        return res.exit == 0;
    }
    
    protected String setupLocalVolume(MachineVolumeEO vol)
    {
        File source = new File(this.localDir, vol.getSource() + ".qcow2");
        if (! source.exists())
        {
            List<String> args = null;
            switch (vol.getMode())
            {
                case CLONE:
                    File parent = new File(this.localDir, vol.getSourceParent() + ".qcow2");
                    if (! parent.exists()) throw new VirtError("Cannot find parent volume: " + vol.getSourceParent());
                    logger.info("Creating local qcow2 volumne " + source.getAbsolutePath() + " cloned from " + parent.getAbsolutePath());
                    source.getParentFile().mkdirs();
                    args = Arrays.asList("create","-f", "qcow2", "-b", parent.getAbsolutePath(), "-F", "qcow2", source.getAbsolutePath());
                    break;
                case CREATE:
                    logger.info("Creating new local qcow2 volumne " + source.getAbsolutePath());
                    source.getParentFile().mkdirs();
                    args = Arrays.asList("create","-f", "qcow2", source.getAbsolutePath(), String.valueOf(vol.getSize()));
                    break;
                case ATTACH:
                    if (! source.exists()) throw new VirtError("Cannot attach, no such volume: " + source.getAbsolutePath());
                    logger.info("Attaching ceph volume " + source);
                    break;
                default:
                    throw new VirtError("Unsupported volume mode" + vol.getMode());
            }
            if (args != null)
            {
                ExecResult res = ExecUtil.exec("/usr/bin/qemu-img", args);
                logger.debug("qemu-img exited with: " + res.exit + "\n" + res.out + "\n" + res.error);
                if (res.exit != 0) throw new VirtError("Failed to create qcow2 image: " + res.exit + ", " + res.error);
            }
        }
        return source.getAbsolutePath();
    }
}
