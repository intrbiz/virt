package com.intrbiz.virt.manager.virt;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.system.sysfs.SysFs;
import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.cluster.model.MachineStatus;
import com.intrbiz.virt.config.VirtManagerCfg;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineInterfaceEO;
import com.intrbiz.virt.event.model.MachineVolumeEO;
import com.intrbiz.virt.libvirt.CloseListener;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.AuthDef;
import com.intrbiz.virt.libvirt.model.definition.DiskDef;
import com.intrbiz.virt.libvirt.model.definition.DriverDef;
import com.intrbiz.virt.libvirt.model.definition.InterfaceDef;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;
import com.intrbiz.virt.libvirt.model.definition.SecretDef;
import com.intrbiz.virt.libvirt.model.definition.SourceDef;
import com.intrbiz.virt.libvirt.model.definition.SysInfoDef;
import com.intrbiz.virt.libvirt.model.definition.SysInfoEntryDef;
import com.intrbiz.virt.libvirt.model.definition.TargetDef;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;
import com.intrbiz.virt.manager.HostManagerContext;
import com.intrbiz.virt.manager.HostMetadataStoreContext;
import com.intrbiz.virt.manager.net.model.BridgedInterfaceInfo;
import com.intrbiz.virt.manager.net.model.DirectInterfaceInfo;
import com.intrbiz.virt.manager.net.model.InterfaceInfo;
import com.intrbiz.virt.manager.net.model.VhostUserInterfaceInfo;
import com.intrbiz.virt.manager.store.model.BlockVolumeInfo;
import com.intrbiz.virt.manager.store.model.CephVolumeInfo;
import com.intrbiz.virt.manager.store.model.FileVolumeInfo;
import com.intrbiz.virt.manager.store.model.FileVolumeInfo.Format;
import com.intrbiz.virt.manager.store.model.VolumeInfo;
import com.intrbiz.virt.manager.virt.model.HostInfo;

public class LibvirtManager implements VirtManager
{
    private static final Logger logger = Logger.getLogger(LibvirtManager.class);

    private String libvirtURL;

    private LibvirtMachineTypes machineTypes;

    private final Timer timer = new Timer();

    private LibVirtAdapter connection;

    private VirtManagerCfg config;
    
    private String cephCacheMode = "writethrough";
    
    private String cephIoMode = "native";
    
    private transient HostManagerContext context;

    public LibvirtManager()
    {
        super();
    }

    @Override
    public void configure(VirtManagerCfg cfg) throws Exception
    {
        this.config = cfg;
        this.libvirtURL = cfg.getStringParameterValue("libvirt.url", "qemu+tcp://root@127.0.0.1:16509/system");
        this.cephCacheMode = cfg.getStringParameterValue("ceph.cache.mode", "writethrough");
        this.cephIoMode = cfg.getStringParameterValue("ceph.io.mode", "threads");
        this.machineTypes = new LibvirtMachineTypes(new File(cfg.getStringParameterValue("virt.types", "/etc/virt/host/types")));
    }

    @Override
    public VirtManagerCfg getConfiguration()
    {
        return this.config;
    }

    @Override
    public void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext)
    {
        this.context = managerContext;
        this.connect();
    }

    public String getLibvirtURL()
    {
        return this.libvirtURL;
    }

    protected LibVirtAdapter getConnection()
    {
        // TODO: block trying to connect ?
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
    public Set<String> getAvailableMachineTypeFamilies()
    {
        return this.machineTypes.getAvailableMachineTypeFamilies();
    }
    
    @Override
    public HostInfo getHostInfo()
    {   
        if (!this.isConnected()) throw new VirtError("Cannot stop machine at this time");
        LibVirtNodeInfo info = this.connection.nodeInfo();
        long definedMemory = 0;
        int runningMachines = 0;
        int definedMachines = 0;
        for (LibVirtDomain domain : this.connection.listDomains())
        {
            definedMachines ++;
            definedMemory += domain.getDomainDef().getMemory().getBytesValue();
            if (domain.isRunning()) runningMachines ++;
        }
        return new HostInfo(info.getCpus(), info.getMemory(), runningMachines, definedMachines, definedMemory, SysFs.sysFs());
    }

    @Override
    public List<MachineState> discoverMachines()
    {
        if (this.isConnected())
            return this.connection.listDomains().stream().map((d) ->
                new MachineState(d.getUUID(), d.getName(), d.isRunning() ? MachineStatus.RUNNING : MachineStatus.STOPPED, d.isAutostart(), d.isPersistent())
            ).collect(Collectors.toList());
        return new LinkedList<MachineState>();
    }
    
    @Override
    public MachineState getMachine(UUID id)
    {
        if (this.isConnected())
        {
            LibVirtDomain d = this.connection.lookupDomainByUuid(id);
            return new MachineState(d.getUUID(), d.getName(), d.isRunning() ? MachineStatus.RUNNING : MachineStatus.STOPPED, d.isAutostart(), d.isPersistent());
        }
        return null;
    }
    
    @Override
    public void terminateMachine(MachineEO machine)
    {
        if (!this.isConnected()) throw new VirtError("Cannot terminate machine at this time");
        LibVirtDomain domain = this.connection.lookupDomainByUuid(machine.getId());
        if (domain != null)
        {
            if (domain.isRunning()) domain.terminate();
            domain.remove();
            // Remove network interfaces
            this.removeInterfaces(machine);
            // Remove volumes
            this.removeVolumes(machine);
        }
    }
    
    @Override
    public void releaseMachine(MachineEO machine)
    {
        if (!this.isConnected()) throw new VirtError("Cannot release machine at this time");
        LibVirtDomain domain = this.connection.lookupDomainByUuid(machine.getId());
        if (domain != null)
        {
            if (domain.isRunning()) domain.terminate();
            domain.remove();
            // Release network interfaces
            this.releaseInterfaces(machine);
            // Release volumes
            this.releaseVolumes(machine);
        }
    }
    
    @Override
    public void start(MachineEO machine)
    {
        if (!this.isConnected()) throw new VirtError("Cannot start machine at this time");
        LibVirtDomain domain = this.connection.lookupDomainByUuid(machine.getId());
        if (domain != null && (! domain.isRunning()))
        {
            startInterfaces(machine);
            domain.start();
        }
    }
    
    @Override
    public void stop(MachineEO machine, boolean force)
    {
        if (!this.isConnected()) throw new VirtError("Cannot stop machine at this time");
        LibVirtDomain domain = this.connection.lookupDomainByUuid(machine.getId());
        if (domain != null) 
        {
            if (force) domain.terminate();
            else domain.powerOff();
            this.stopInterfaces(machine);
        }
    }
    
    @Override
    public void reboot(MachineEO machine, boolean force)
    {
        if (!this.isConnected()) throw new VirtError("Cannot reboot machine at this time");
        LibVirtDomain domain = this.connection.lookupDomainByUuid(machine.getId());
        if (domain != null && domain.isRunning()) 
        {
            this.startInterfaces(machine);
            if (force) domain.reset();
            else domain.reboot();   
        }
    }
    
    @Override
    public void attachVolumeToMachine(MachineEO machine, MachineVolumeEO attachVolume)
    {
        if (!this.isConnected()) throw new VirtError("Cannot attach volume at this time");
        // Lookup the domain
        LibVirtDomain domain = this.getConnection().lookupDomainByUuid(machine.getId());
        if (domain == null) throw new VirtError("Cannot attach volume, the machine " + machine.getId() + " is not defined on this host");
        // Get the disk attachment information and attach
        domain.attachDevice(this.fromVolumeInfo(this.context.getStoreManager().createOrAttachVolume(attachVolume), attachVolume.getName()));
    }
    
    @Override
    public void createMachine(MachineEO machine)
    {
        if (!this.isConnected()) throw new VirtError("Cannot create machine at this time");
        // load our VM template
        LibVirtDomainDef domainDef = this.machineTypes.loadMachineType(machine.getMachineTypeFamily());
        if (domainDef == null) throw new VirtError("Failed to load machine type '" + machine.getMachineTypeFamily() + "' template");
        // validate we can create dependent devices
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            if (! this.context.getNetManager().isSupported(nic.getNetwork())) throw new VirtError("Network type " + nic.getNetwork().getType() + " is not supported");
        }
        for (MachineVolumeEO vol : machine.getVolumes())
        {
            if (! this.context.getStoreManager().isSupported(vol)) throw new VirtError("Volume type " + vol.getType() + " is not supported");
        }
        // set core domain details
        domainDef.setUuid(machine.getId().toString());
        domainDef.setName(("m-" + machine.getId()).toLowerCase());
        domainDef.setTitle(machine.getAccount().getName() + "::" + machine.getName());
        domainDef.getVcpu().setCount(machine.getCpus());
        domainDef.getCurrentMemory().setBytesValue(machine.getMemory());
        domainDef.getMemory().setBytesValue(machine.getMemory());
        // configure metadata
        SysInfoDef sysInfo = domainDef.getSysinfo();
        if (sysInfo != null)
        {
            sysInfo.getBaseBoard().add(new SysInfoEntryDef("version", machine.getMachineType()));
            sysInfo.getSystem().add(new SysInfoEntryDef("serial", "ds=intrbiz;a=" + machine.getCfgIPv4() + "/16;s=172.16.0.1"));
        }
        // configure the cfgMac (must be first)
        domainDef.getDevices().addDevice(this.fromInterfaceInfo(this.context.getNetManager().setupGuestMetadataNIC(machine)));
        // create our network interfaces
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            domainDef.getDevices().addDevice(this.fromInterfaceInfo(this.context.getNetManager().setupGuestNIC(nic)));
        }
        // create our storage volumes
        for (MachineVolumeEO vol : machine.getVolumes())
        {
            domainDef.getDevices().addDevice(this.fromVolumeInfo(this.context.getStoreManager().createOrAttachVolume(vol), vol.getName()));
        }
        // Define the domain
        logger.info("Defining VM from domain definition: \n" + domainDef);
        LibVirtDomain domain = this.getConnection().addDomain(domainDef);
        logger.info("Defined libvirt domain: " + domain.getUUID() + " " + domain.getName());
    }

    /**
     * Convert a VolumeInfo to a libvirt DiskDef
     */
    protected DiskDef fromVolumeInfo(VolumeInfo vol, String targetDev)
    {
        if (vol instanceof CephVolumeInfo)
        {
            CephVolumeInfo ceph = (CephVolumeInfo) vol;
            return new DiskDef("network", "disk", DriverDef.raw(this.cephCacheMode, this.cephIoMode), SourceDef.rbd(ceph.getHosts().split(", ?"), 6789, ceph.getSource()), TargetDef.scsi(targetDev), new AuthDef("libvirt", new SecretDef("ceph", ceph.getAuth())));
        }
        else if (vol instanceof FileVolumeInfo)
        {
            FileVolumeInfo file = (FileVolumeInfo) vol;
            return new DiskDef("file", "disk", file.getFormat() == Format.QCOW2 ? DriverDef.qcow2("none") : DriverDef.raw("none"), SourceDef.file(file.getPath()), TargetDef.scsi(targetDev));
        }
        else if (vol instanceof BlockVolumeInfo)
        {
            BlockVolumeInfo block = (BlockVolumeInfo) vol;
            return new DiskDef("block", "disk", DriverDef.raw("none"), SourceDef.device(block.getDevicePath()), TargetDef.scsi(targetDev));
        }
        throw new VirtError("Unsupported VolumeInfo type: " + vol);
    }
    
    protected void removeVolumes(MachineEO machine)
    {
        for (MachineVolumeEO vol : machine.getVolumes())
        {
            this.context.getStoreManager().removeVolume(vol);
        }
    }
    
    protected void releaseVolumes(MachineEO machine)
    {
        for (MachineVolumeEO vol : machine.getVolumes())
        {
            this.context.getStoreManager().releaseVolume(vol);
        }
    }
    
    protected void startInterfaces(MachineEO machine)
    {
        // start the metadata interface
        this.context.getNetManager().startGuestMetadataNIC(machine);
        // start the network interfaces
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            this.context.getNetManager().startGuestNIC(nic);
        }
    }
    
    protected void stopInterfaces(MachineEO machine)
    {
        // start the metadata interface
        this.context.getNetManager().stopGuestMetadataNIC(machine);
        // start the network interfaces
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            this.context.getNetManager().stopGuestNIC(nic);
        }
    }
    
    protected void removeInterfaces(MachineEO machine)
    {
        // start the metadata interface
        this.context.getNetManager().removeGuestMetadataNIC(machine);
        // start the network interfaces
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            this.context.getNetManager().removeGuestNIC(nic);
        }
    }
    
    protected void releaseInterfaces(MachineEO machine)
    {
        // start the metadata interface
        this.context.getNetManager().releaseGuestMetadataNIC(machine);
        // start the network interfaces
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            this.context.getNetManager().releaseGuestNIC(nic);
        }
    }

    /**
     * Convert an InterfaceInfo to a libvirt interface def
     */
    protected InterfaceDef fromInterfaceInfo(InterfaceInfo nic)
    {
        if (nic instanceof BridgedInterfaceInfo)
        {
            BridgedInterfaceInfo bridged = (BridgedInterfaceInfo) nic;
            return InterfaceDef.virtioBridge(bridged.getMac(), bridged.getBridge(), TargetDef.dev("v-" + bridged.getMac().replace(":", "")));
        }
        else if (nic instanceof DirectInterfaceInfo)
        {
            DirectInterfaceInfo direct = (DirectInterfaceInfo) nic;
            return InterfaceDef.direct(direct.getMac(), direct.getDevice(), direct.getMode());
        }
        else if (nic instanceof VhostUserInterfaceInfo)
        {
            VhostUserInterfaceInfo vhostUser = (VhostUserInterfaceInfo) nic;
            return InterfaceDef.vhostUser(vhostUser.getMac(), vhostUser.getPath(), vhostUser.isServer());
        }
        throw new VirtError("Unsupported InerfaceInfo type: " + nic);
    }
}
