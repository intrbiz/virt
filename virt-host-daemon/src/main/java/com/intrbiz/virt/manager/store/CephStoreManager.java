package com.intrbiz.virt.manager.store;

import static com.intrbiz.system.exec.Command.*;
import static com.intrbiz.virt.manager.store.StoreManager.TYPES.*;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.intrbiz.Util;
import com.intrbiz.configuration.CfgParameter;
import com.intrbiz.system.exec.Command;
import com.intrbiz.system.exec.Result;
import com.intrbiz.system.exec.SystemExecutionException;
import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.config.StoreManagerCfg;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.event.model.MachineVolumeEO.VolumeMode;
import com.intrbiz.virt.event.model.PersistentVolumeEO;
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
    
    private static final String STRIPE_SIZE = "--stripe-size";
    
    private static final String STRIPE_COUNT = "--stripe-count";
    
    private static final String MEGABYTES = "M";
    
    private static final int[] RBD_SUCCESS_CODES = { 0 };
    
    private static final int DEFAULT_STRIPE_SIZE = 4096; // 4MiB
    
    private static final int DEFAULT_STRIPE_COUNT = 1;
    
    private static final Logger logger = Logger.getLogger(CephStoreManager.class);
    
    protected String cephHosts;
    
    protected String cephAuth;
    
    protected Map<String, String> cephMachinePools = new TreeMap<String, String>();
    
    protected Map<String, CephStripeSettings> cephStripeSettings = new TreeMap<String, CephStripeSettings>();
    
    protected String cephPersistentPool;
    
    protected CephStripeSettings cephPersistentStripeSettings;
    
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
        this.cephAuth = cfg.getStringParameterValue("ceph.auth.uuid", "6f3128fa-fada-463a-989f-b965c83e5da9");
        // pool maps
        this.cephPersistentPool = cfg.getStringParameterValue("ceph.persistent.pool.name", "persistent");
        this.cephPersistentStripeSettings = new CephStripeSettings(cfg.getStringParameterValue("ceph.persistent.stripe", DEFAULT_STRIPE_SIZE + "*" + DEFAULT_STRIPE_COUNT));
        for (CfgParameter param : config.getParameters())
        {
            if (param.getName().startsWith("ceph.machine"))
            {
                if (param.getName().endsWith("pool.name"))
                {
                    String type = Util.coalesceEmpty(param.getName().replace("ceph.machine.", "").replace(".pool.name", ""), "*");
                    this.cephMachinePools.put(type, param.getValueOrText());
                }
                else if (param.getName().endsWith("stripe"))
                {
                    String type = Util.coalesceEmpty(param.getName().replace("ceph.machine.", "").replace(".stripe", ""), "*");
                    this.cephStripeSettings.put(type, new CephStripeSettings(param.getValueOrText()));
                }
            }
        }
        if (! this.cephMachinePools.containsKey("*"))
            this.cephMachinePools.put("*", "machine");
        if (! this.cephStripeSettings.containsKey("*"))
            this.cephStripeSettings.put("*", new CephStripeSettings(DEFAULT_STRIPE_SIZE, DEFAULT_STRIPE_COUNT));
        logger.info("Using Ceph pool " + this.cephPersistentPool + " for persistent volumes");
        logger.info("Using Ceph stripe settings " + this.cephPersistentStripeSettings + " for persistent volumes");
        logger.info("Using Ceph pools " + this.cephMachinePools + " for machine volumes");
        logger.info("Using Ceph stripe settings " + this.cephStripeSettings + " for machine volumes");
    }
    
    @Override
    public VolumeInfo createOrAttachVolume(String machineFamily, MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                return this.createCephVolume(machineFamily, vol);
        }
        return super.createOrAttachVolume(machineFamily, vol);
    }
    
    @Override
    public void releaseVolume(String machineFamily, MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                this.releaseCephVolume(machineFamily, vol);
                return;
        }
        super.releaseVolume(machineFamily, vol);
    }
    
    @Override
    public void removeVolume(String machineFamily, MachineVolumeEO vol)
    {
        switch (vol.getType())
        {
            case CEPH:
                this.removeCephVolume(machineFamily, vol);
                return;
        }
        super.removeVolume(machineFamily, vol);
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
    
    private void releaseCephVolume(String machineFamily, MachineVolumeEO vol)
    {
        // Nothing to do to release a Ceph volume
    }
    
    private void createPersistentCephVolume(PersistentVolumeEO pvol)
    {
        CephStripeSettings stripe = this.cephPersistentStripeSettings;
        // Create a persistent Ceph volume
        String source = this.prependPersistentPool(pvol.getSource());
        if (cephVolumeExists(source))
        {
            try
            {
                logger.info("Creating ceph volumne " + source + " size " + pvol.getSize() + " stripe " + stripe);
                this.executor.expect(command(RBD, CREATE, STRIPE_SIZE, stripe.getStripeSizeArg(), STRIPE_COUNT, stripe.getStripeCountArg(), OBJECT_SIZE, stripe.getObjectSizeArg(), SIZE,  (pvol.getSize() / 1_000_000) + MEGABYTES, source), RBD_SUCCESS_CODES);
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

    private CephVolumeInfo createCephVolume(String machineFamily, MachineVolumeEO vol)
    {
        CephStripeSettings stripe = this.getCephStripeSettingsForMachineType(machineFamily);
        String source = null;
        Command command = null;
        switch (vol.getMode())
        {
            case CLONE:
                // Clone a machine volume
                source = this.prependMachinePool(machineFamily, vol.getSource());
                if (! cephVolumeExists(source))
                {
                    if (vol.getSourceParent() == null) throw new VirtError("Cannot clone from null source parent");
                    String parent = this.prependMachinePool(machineFamily, vol.getSourceParent());
                    if (! cephVolumeExists(parent)) throw new VirtError("Cannot clone, no such volume: " + parent);
                    logger.info("Cloning ceph volumne " + source + " from snapshot " + parent + " stripe " + stripe);
                    command = command(RBD, CLONE, STRIPE_SIZE, stripe.getStripeSizeArg(), STRIPE_COUNT, stripe.getStripeCountArg(), OBJECT_SIZE, stripe.getObjectSizeArg(), parent, source);
                }
                break;
            case CREATE:
                // Create a machine volume
                source = this.prependMachinePool(machineFamily, vol.getSource());
                if (! cephVolumeExists(source))
                {
                    logger.info("Creating ceph volumne " + source + " size " + vol.getSize() + " stripe " + stripe);
                    command = command(RBD, CREATE, STRIPE_SIZE, stripe.getStripeSizeArg(), STRIPE_COUNT, stripe.getStripeCountArg(), OBJECT_SIZE, stripe.getObjectSizeArg(), SIZE,  (vol.getSize() / 1_000_000) + MEGABYTES, source);
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
    
    private void removeCephVolume(String machineFamily, MachineVolumeEO vol)
    {
        // Remove Ceph volume which are non-persistent
        if (vol.getMode() == VolumeMode.CLONE || vol.getMode() == VolumeMode.CREATE)
        {
            String source = this.prependMachinePool(machineFamily, vol.getSource());
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
    
    protected CephStripeSettings getCephStripeSettingsForMachineType(String family)
    {
        if (this.cephStripeSettings.containsKey(family))
            return this.cephStripeSettings.get(family);
        return this.cephStripeSettings.get("*");
    }
    
    protected String getCephPoolForMachineType(String family)
    {
        if (this.cephMachinePools.containsKey(family))
            return this.cephMachinePools.get(family);
        return this.cephMachinePools.get("*");
    }
    
    protected String prependMachinePool(String family, String source)
    {
        return this.getCephPoolForMachineType(family) + "/" + source;
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
    
    public static class CephStripeSettings
    {
        private final int stripeSize;
        
        private final int stripeCount;
        
        private final int objectSize;

        public CephStripeSettings(int stripeSize, int stripeCount)
        {
            super();
            this.stripeSize = stripeSize;
            this.stripeCount = stripeCount;
            this.objectSize = stripeSize * stripeCount;
        }
        
        public CephStripeSettings(String setting)
        {
            super();
            String[] parts = setting.split("[*:]");
            this.stripeSize = Integer.parseInt(parts[0]);
            this.stripeCount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            this.objectSize = parts.length > 2 ? Integer.parseInt(parts[2]) : this.stripeSize * this.stripeCount;
        }
        
        public int getStripeSize()
        {
            return this.stripeSize;
        }
        
        public int getStripeCount()
        {
            return this.stripeCount;
        }
        
        public int getObjectSize()
        {
            return this.objectSize;
        }
        
        public String getStripeSizeArg()
        {
            return this.stripeSize + "K";
        }
        
        public String getStripeCountArg()
        {
            return String.valueOf(this.stripeCount);
        }
        
        public String getObjectSizeArg()
        {
            return this.objectSize + "K";
        }
        
        public String toString()
        {
            return this.stripeSize + "*" + this.stripeCount + ":" + this.objectSize;
        }
    }
}
