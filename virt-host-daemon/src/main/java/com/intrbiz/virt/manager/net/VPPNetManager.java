package com.intrbiz.virt.manager.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.config.NetManagerCfg;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineInterfaceEO;
import com.intrbiz.virt.event.model.NetworkEO;
import com.intrbiz.virt.libvirt.model.definition.SourceDef.MacVTapMode;
import com.intrbiz.virt.manager.HostManagerContext;
import com.intrbiz.virt.manager.HostMetadataStoreContext;
import com.intrbiz.virt.manager.net.model.DirectInterfaceInfo;
import com.intrbiz.virt.manager.net.model.InterfaceInfo;
import com.intrbiz.virt.manager.net.model.VhostUserInterfaceInfo;
import com.intrbiz.virt.vpp.VPPDaemonClient;
import com.intrbiz.virt.vpp.VPPDaemonClientAPIException;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.VhostUserMode;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.recipe.VMInterface;
import com.intrbiz.vpp.recipe.VMInterfaceType;
import com.intrbiz.vpp.recipe.VMNetworkId;
import com.intrbiz.vpp.recipe.VMNetworks;
import com.intrbiz.vpp.recipe.VethHostInterface;
import com.intrbiz.vpp.recipe.VhostUserInterface;
import com.intrbiz.vpp.util.RecipeReader;
import com.intrbiz.vpp.util.RecipeWriter;

public class VPPNetManager implements NetManager
{
    private static final String VM_NETWORKS_KEY = "vpp.vm.networks";
    
    private static final String VM_NETWORKS_RECIPE_NAME = "vm.networks";
    
    private static final Logger logger = Logger.getLogger(VPPNetManager.class);
    
    private final Timer timer = new Timer();
    
    private final Set<String> supportedTypes = Collections.unmodifiableSet(new TreeSet<String>(Arrays.asList("vxlan", "public", "transfer", "service")));
    
    private String interconnectInterface;
    
    private String metadataServerInterface;
    
    private String metadataVMInterface;
    
    private IPv4CIDR interconnectCIDR;
    
    private VMNetworkId publicNetworkId;
    
    private VMNetworkId serviceNetworkId;
    
    private VMNetworkId transferNetworkId;
    
    private NetManagerCfg config;
    
    private VMNetworks vmNetworks;
    
    private String vppDaemonUrl;
    
    private long vppApplyInterval = 30_000L;
    
    private transient TimerTask applyTask;
    
    private transient HostMetadataStoreContext metadataContext;
    
    private transient VPPDaemonClient vppDaemonClient;
    
    public VPPNetManager()
    {
        super();
    }
    
    @Override
    public void configure(NetManagerCfg cfg) throws Exception
    {
        this.config = cfg;
        this.metadataServerInterface = cfg.getStringParameterValue("metadata.server.interface", "metadata_srv");
        this.metadataVMInterface = cfg.getStringParameterValue("metadata.vm.interface", "metadata_vms");
        this.interconnectInterface = cfg.getStringParameterValue("interconnect.interface", "uplink_vpp");
        this.interconnectCIDR = IPv4CIDR.fromString(cfg.getStringParameterValue("interconnect.address", "172.22.40.10/24"));
        this.publicNetworkId = new VMNetworkId(cfg.getIntParameterValue("public.network.id", 20));
        this.serviceNetworkId = new VMNetworkId(cfg.getIntParameterValue("service.network.id", 30));
        this.transferNetworkId = new VMNetworkId(cfg.getIntParameterValue("transfer.network.id", 40));
        this.vppDaemonUrl = cfg.getStringParameterValue("vpp.daemon.url", "http://localhost:8989/");
        this.vppApplyInterval = cfg.getLongParameterValue("vpp.apply.interval", 30_000L);
    }

    @Override
    public NetManagerCfg getConfiguration()
    {
        return this.config;
    }
    
    public void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext)
    {
        this.metadataContext = metadataContext;
        // Set up our VPP Daemon Client
        this.vppDaemonClient = new VPPDaemonClient(this.vppDaemonUrl);
        // load our previous state if it is stored
        this.loadVPPRecipes();
        // setup our VPP recipes
        if (this.vmNetworks == null)
        {
            this.vmNetworks = new VMNetworks(this.interconnectInterface, this.interconnectCIDR, VMInterfaceType.VETH);
        }
        // Add in our infrastructure networks
        this.vmNetworks.addNetwork(new VMNetworkId(this.publicNetworkId.getValue()));
        this.vmNetworks.addNetwork(new VMNetworkId(this.serviceNetworkId.getValue()));
        this.vmNetworks.addNetwork(new VMNetworkId(this.transferNetworkId.getValue()));
        // apply the state
        this.apply();
        // Setup a timer task to ensure our VPP recipe is running every 30 seconds, in case VPP crashes
        this.applyTask = new TimerTask()
        {
            public void run()
            {
                apply();
            }
        };
        this.timer.scheduleAtFixedRate(this.applyTask, this.vppApplyInterval, this.vppApplyInterval);
    }
    
    public Set<String> getSupportedTypes()
    {
        return supportedTypes;
    }

    public String getInterconnectInterface()
    {
        return interconnectInterface;
    }

    public String getMetadataServerInterface()
    {
        return metadataServerInterface;
    }

    public String getMetadataVMInterface()
    {
        return metadataVMInterface;
    }

    public IPv4CIDR getInterconnectCIDR()
    {
        return interconnectCIDR;
    }

    public VMNetworkId getPublicNetworkId()
    {
        return publicNetworkId;
    }

    public VMNetworkId getServiceNetworkId()
    {
        return serviceNetworkId;
    }

    public VMNetworkId getTransferNetworkId()
    {
        return transferNetworkId;
    }

    public String getInterconnectAddress()
    {
        return this.interconnectCIDR.getAddress().toString();
    }
    
    public void registerRemoteVMHost(String remoteVMHostAddress)
    {
        this.vmNetworks.addRemoteVMHost(IPv4Address.fromString(remoteVMHostAddress));
        this.apply();
    }
    
    public void registerRemoteVMHosts(Set<String> remoteVMHostAddresses)
    {
        for (String remoteVMHostAddress : remoteVMHostAddresses)
        {
            this.vmNetworks.addRemoteVMHost(IPv4Address.fromString(remoteVMHostAddress));
        }
        this.apply();
    }
    
    protected synchronized void apply()
    {
        this.saveVPPRecipes();
        logger.info("Updating VPP VM Networks recipe to:\n" + RecipeWriter.getDefault().toString(this.vmNetworks));
        // Talk to our VPP daemon
        for (int i = 0; i < 2; i++)
        {
            try
            {
                VPPRecipe applied = this.vppDaemonClient.callApplyRecipe().name(VM_NETWORKS_RECIPE_NAME).recipe(this.vmNetworks).execute();
                logger.info("Applied VPP Recipse: " + applied);
                // Successfully applied
                break;
            }
            catch (VPPDaemonClientAPIException e)
            {
                logger.error("Failed to apply VPP recipes, this will get retried", e);
            }
        }
    }
    
    private void saveVPPRecipes()
    {
        this.metadataContext.set(VM_NETWORKS_KEY, RecipeWriter.getDefault().toString(this.vmNetworks));
    }
    
    private void loadVPPRecipes()
    {
        String vmNetworksString = this.metadataContext.get(VM_NETWORKS_KEY);
        if (vmNetworksString != null)
        {
            this.vmNetworks = RecipeReader.getDefault().fromString(VMNetworks.class, vmNetworksString);
            if (this.vmNetworks != null)
            {
                logger.info("Loaded existing VPP VM Networks recipe:\n" + RecipeWriter.getDefault().toString(this.vmNetworks));
            }
        }
    }
    
    @Override
    public Set<String> getSupportedNetworkTypes()
    {
        return this.supportedTypes;
    }
    
    @Override
    public boolean isSupported(NetworkEO net)
    {
        return this.supportedTypes.contains(net.getType());
    }

    @Override
    public void setupNetwork(NetworkEO net)
    {
        logger.info("Setting up network: " + net.getName() + " " + net.getType() + " " + net.getVxlanid());
        if ("vxlan".equals(net.getType()))
        {
            this.vmNetworks.addNetwork(new VMNetworkId(net.getVxlanid()));
        }
        this.apply();
    }

    @Override
    public InterfaceInfo setupGuestMetadataNIC(MachineEO machine)
    {
        // For metadata we can just use a MacVTap in private mode
        String mac = machine.getCfgMac();
        return new DirectInterfaceInfo(mac, this.metadataVMInterface, MacVTapMode.PRIVATE);
    }

    @Override
    public InterfaceInfo setupGuestNIC(MachineInterfaceEO nic)
    {
        logger.info("Setting up guest interface: " + nic.getMac() + " " + nic.getName() + " " + nic.getNetwork().getType() + " " + nic.getNetwork().getVxlanid());
        if ("vxlan".equals(nic.getNetwork().getType()))
        {
            VMNetworkId network = new VMNetworkId(nic.getNetwork().getVxlanid());
            this.vmNetworks.addNetwork(network);
            return this.setupGuestNIC(nic, network);
        }
        else if ("public".equalsIgnoreCase(nic.getNetwork().getType()))
        {
            return this.setupGuestNIC(nic, this.publicNetworkId);
        }
        else if ("transfer".equalsIgnoreCase(nic.getNetwork().getType()))
        {
            return this.setupGuestNIC(nic, this.transferNetworkId);
        }
        else if ("service".equalsIgnoreCase(nic.getNetwork().getType()))
        {
            return this.setupGuestNIC(nic, this.serviceNetworkId);
        }
        throw new VirtError("Cannot create guest interface for network of type: " + nic.getNetwork().getType());
    }
    
    protected InterfaceInfo setupGuestNIC(MachineInterfaceEO nic, VMNetworkId network)
    {
        VMInterface iface = this.vmNetworks.addVMInterface(MACAddress.fromString(nic.getMac()), new VMNetworkId(nic.getNetwork().getVxlanid()));
        this.apply();
        return toInterfaceInfo(iface);
    }
    
    protected InterfaceInfo toInterfaceInfo(VMInterface vmInterface)
    {
        VPPInterfaceRecipe iface = vmInterface.getVmInterface();
        if (iface instanceof VhostUserInterface)
        {
            VhostUserInterface vhostUser = (VhostUserInterface) iface;
            return new VhostUserInterfaceInfo(vmInterface.getVmMACAddress().toString(), vhostUser.getSocket(), vhostUser.getMode() == VhostUserMode.CLIENT);
        }
        else if (iface instanceof VethHostInterface)
        {
            VethHostInterface veth = (VethHostInterface) iface;
            return new DirectInterfaceInfo(vmInterface.getVmMACAddress().toString(), veth.getHostInterfacePeerName(), MacVTapMode.PRIVATE);
        }
        return null;
    }
    
    @Override
    public void startGuestMetadataNIC(MachineEO machine)
    {
        this.setupGuestMetadataNIC(machine);
    }

    @Override
    public void startGuestNIC(MachineInterfaceEO nic)
    {
        this.setupGuestNIC(nic);
    }

    @Override
    public void stopGuestMetadataNIC(MachineEO machine)
    {
        // Nothing to do currently
    }

    @Override
    public void stopGuestNIC(MachineInterfaceEO nic)
    {   
        // Nothing to do currently
    }

    @Override
    public void removeGuestMetadataNIC(MachineEO machine)
    {
        // TODO
    }

    @Override
    public void removeGuestNIC(MachineInterfaceEO nic)
    {
        // TODO
    }
}
