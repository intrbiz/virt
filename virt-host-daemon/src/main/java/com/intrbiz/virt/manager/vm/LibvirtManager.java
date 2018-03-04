package com.intrbiz.virt.manager.vm;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.cluster.model.MachineStatus;
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
import com.intrbiz.virt.libvirt.model.definition.TargetDef;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;
import com.intrbiz.virt.manager.net.NetManager;
import com.intrbiz.virt.manager.store.StoreManager;

public class LibvirtManager implements VirtManager
{
    private static final Logger logger = Logger.getLogger(LibvirtManager.class);
    
    private String libvirtURL;
    
    private LibvirtMachineTypes machineTypes;
    
    private final Timer timer = new Timer();
    
    private LibVirtAdapter connection;
    
    private String cephHosts = "172.26.30.31,172.26.30.32,172.26.30.33,172.26.30.3,172.26.30.34,172.26.30.35,172.26.30.36,172.26.30.37";
    
    private String cephAuth = "6f3128fa-fada-463a-989f-b965c83e5da9";
    
    private String nicTargetPrefix = "vm";
    
    public LibvirtManager()
    {
        super();
        this.libvirtURL = "qemu+tcp://root@127.0.0.1:16509/system";
        this.machineTypes = new LibvirtMachineTypes(new File(System.getProperty("virt.types", "/etc/virt/types")));
    }
    
    @Override
    public void start()
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
     * Connect to the LibVirt daemon on the host
     * and do the initial setup
     */
    private void connect()
    {
        logger.trace("Connecting to " + this.libvirtURL);
        try
        {
            this.connection = LibVirtAdapter.connect(this.libvirtURL);
            // add a close listener to reconnect
            this.connection.addCloseListener(new CloseListener() {
                @Override
                public void onClose(LibVirtAdapter adapter)
                {
                    // schedule reconnect
                    logger.info("Scheduling reconnect to libvirt");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run()
                        {
                            connect();
                        }
                    }, 
                    1_000L);
                    connection = null;
                }                
            });
        }
        catch (Exception e)
        {
            logger.warn("Error connecting to host", e);
            // schedule reconnect
            logger.info("Scheduling reconnect to libvirt");
            timer.schedule(new TimerTask() {
                @Override
                public void run()
                {
                    connect();
                }
            }, 
            5_000L);
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
    public int getHostCPUs()
    {
        if (this.isConnected())
        {
            LibVirtNodeInfo info = this.connection.nodeInfo();
            return info.getCpus();
        }
        return 0;
    }
    
    @Override
    public long getHostMemory()
    {
        
        if (this.isConnected())
        {
            LibVirtNodeInfo info = this.connection.nodeInfo();
            return info.getMemory();
        }
        return 0;
    }
    
    @Override
    public int getRunningMachines()
    {
        if (this.isConnected())
        {
            return (int) this.connection.listDomains().stream()
                    .filter((d) -> d.isRunning()).count();
        }
        return 0;
    }
    
    @Override
    public long getDefinedMemory()
    {
        if (this.isConnected())
        {
            return this.connection.listDomains().stream()
                    .collect(Collectors.summingLong((d) -> d.getDomainDef().getCurrentMemory().getBytesValue()));
        }
        return 0;
    }
    
    @Override
    public List<MachineState> getMachineStates()
    {
        if (this.isConnected())
        {
            return this.connection.listDomains().stream().map((d) -> {
                return new MachineState(
                        d.getUUID(), 
                        d.getName(), 
                        d.isRunning() ? MachineStatus.RUNNING : MachineStatus.STOPPED,
                        d.isAutostart(), 
                        d.isPersistent()
                );
            }).collect(Collectors.toList());
        }
        return new LinkedList<MachineState>();
    }

    @Override
    public void createMachine(MachineEO machine, StoreManager storeManager, NetManager netManager)
    {
        if (! this.isConnected())
            throw new VirtError("Cannot create machine at this time");
        // load our VM template
        LibVirtDomainDef domainDef = this.machineTypes.loadMachineType(machine.getMachineTypeFamily());
        if (domainDef == null) throw new VirtError("Failed to load machine type '" + machine.getMachineTypeFamily() + "' template");
        // validate we can create dependent devices
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            if (! netManager.isSupported(nic.getNetwork()))
                throw new VirtError("Network type " + nic.getNetwork().getType() + " is not supported");
        }
        for (MachineVolumeEO vol : machine.getVolumes())
        {
            if (! storeManager.isSupported(vol))
                throw new VirtError("Volume type " + vol.getType() + " is not supported");
        }
        // set core domain details
        domainDef.setUuid(machine.getId().toString());
        domainDef.setName(("m-" + machine.getId()).toLowerCase());
        domainDef.getVcpu().setCount(machine.getCpus());
        domainDef.getCurrentMemory().setBytesValue(machine.getMemory());
        // machine type families can support simple resizing within the same family, to an extent
        // allow for machine types which exceed the max memory of the family template, with the trade off that they ca't be resized
        if (domainDef.getMemory().getBytesValue() < machine.getMemory())
            domainDef.getMemory().setBytesValue(machine.getMemory());
        // configure the cfgMac (must be first)
        domainDef.getDevices().getInterfaces().get(0).getMac().setAddress(machine.getCfgMac());
        domainDef.getDevices().getInterfaces().get(0).setTarget(TargetDef.dev(this.nicTarget(machine.getCfgMac())));
        // create our networks
        for (MachineInterfaceEO nic : machine.getInterfaces())
        {
            domainDef.getDevices().getInterfaces().add(
                    this.createInterface(nic, netManager)
            );
        }
        // create our storage volumes
        for (MachineVolumeEO vol : machine.getVolumes())
        {
            domainDef.getDevices().getDisks().add(
                    this.creatDisk(vol, storeManager)
            );
        }
        // create the vm
        logger.info("Creating VM from domain definition: \n" + domainDef);
        LibVirtDomain domain = this.connection.addDomain(domainDef);
        domain.start();
        logger.info("Created libvirt domain: " + domain.getUUID() + " " + domain.getName() + " running: " + domain.isRunning());
    }
    
    @Override
    public void rebootMachine(UUID id)
    {
        if (! this.isConnected())
            throw new VirtError("Cannot reboot machine at this time");
        // lookup the domain
        LibVirtDomain domain = this.connection.lookupDomainByUuid(id);
        if (domain == null)
            throw new VirtError("Cannot find machine: " + id);
        // reboot the guest
        domain.reboot();
    }

    @Override
    public void startMachine(UUID id)
    {
        if (! this.isConnected())
            throw new VirtError("Cannot start machine at this time");
        // lookup the domain
        LibVirtDomain domain = this.connection.lookupDomainByUuid(id);
        if (domain == null)
            throw new VirtError("Cannot find machine: " + id);
        // start the guest
        domain.start();
    }

    @Override
    public void stopMachine(UUID id)
    {
        if (! this.isConnected())
            throw new VirtError("Cannot stop machine at this time");
        // lookup the domain
        LibVirtDomain domain = this.connection.lookupDomainByUuid(id);
        if (domain == null)
            throw new VirtError("Cannot find machine: " + id);
        // poweroff the guest
        domain.powerOff();
    }

    @Override
    public void destroyMachine(MachineEO machine, StoreManager storeManager, NetManager netManager)
    {
        // TODO
        
    }

    protected DiskDef creatDisk(MachineVolumeEO vol, StoreManager storeManager)
    {
        if ("local".equals(vol.getType()))
        {
            return this.createLocalDisk(vol, storeManager);
        }
        else if ("ceph".equals(vol.getType()))
        {
            return this.createCephDisk(vol, storeManager);
        }
        else
        {
            throw new VirtError("Cannot use volume type " + vol.getType() + " in machine");
        }
    }
    
    protected DiskDef createCephDisk(MachineVolumeEO vol, StoreManager storeManager)
    {
        String source = storeManager.setupVolume(vol);
        return new DiskDef("network", "disk", 
                DriverDef.raw(), 
                SourceDef.rbd(this.cephHosts, 6789, source), 
                TargetDef.scsi(vol.getName()), 
                new AuthDef("libvirt", new SecretDef("ceph", this.cephAuth)));
    }
    
    protected DiskDef createLocalDisk(MachineVolumeEO vol, StoreManager storeManager)
    {
        String path = storeManager.setupVolume(vol);
        return new DiskDef("file", "disk", DriverDef.qcow2(), SourceDef.file(path), TargetDef.scsi(vol.getName()));
    }
    
    protected InterfaceDef createInterface(MachineInterfaceEO nic, NetManager netManager)
    {
        String bridge = netManager.setupNetwork(nic.getNetwork());
        return InterfaceDef.virtioBridge(nic.getMac(), bridge, TargetDef.dev(this.nicTarget(nic.getMac())));
    }
    
    protected String nicTarget(String mac)
    {
        return this.nicTargetPrefix + mac.replace(":", "");
    }
    
    
}
