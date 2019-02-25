package com.intrbiz.virt.manager.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.cluster.model.HostState;
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
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.VNI;
import com.intrbiz.vpp.api.model.VhostUserMode;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.recipe.Bridge;
import com.intrbiz.vpp.recipe.BridgeInterface;
import com.intrbiz.vpp.recipe.HostInterface;
import com.intrbiz.vpp.recipe.VXLANTunnel;
import com.intrbiz.vpp.recipe.VethHostInterface;
import com.intrbiz.vpp.recipe.VhostUserInterface;

public class VPPNetManager implements NetManager
{    
    private static final Logger logger = Logger.getLogger(VPPNetManager.class);
    
    private final Set<String> supportedTypes = Collections.unmodifiableSet(new TreeSet<String>(Arrays.asList("vxlan", "public", "transfer", "service")));
    
    private String interconnectInterface;
    
    private String metadataServerInterface;
    
    private String metadataVMInterface;
    
    private IPv4CIDR interconnectCIDR;
    
    private VNI publicNetworkId;
    
    private VNI serviceNetworkId;
    
    private VNI transferNetworkId;
    
    private NetManagerCfg config;
    
    private String vppDaemonUrl;
    
    private transient HostManagerContext hostManagerContext;
    
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
        this.publicNetworkId = new VNI(cfg.getIntParameterValue("public.network.id", 20));
        this.serviceNetworkId = new VNI(cfg.getIntParameterValue("service.network.id", 30));
        this.transferNetworkId = new VNI(cfg.getIntParameterValue("transfer.network.id", 40));
        this.vppDaemonUrl = cfg.getStringParameterValue("vpp.daemon.url", "http://localhost:8989/");
    }

    @Override
    public NetManagerCfg getConfiguration()
    {
        return this.config;
    }
    
    public void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext)
    {
        this.hostManagerContext = managerContext;
        this.metadataContext = metadataContext;
        // Set up our VPP Daemon Client
        this.vppDaemonClient = new VPPDaemonClient(this.vppDaemonUrl);
        // Set up our base recipes for interconnect and built in bridges
        this.updateRecipe(new HostInterface("interconnect", this.interconnectInterface, MTU.JUMBO, this.interconnectCIDR));
        this.updateRecipe(new Bridge(bridgeName(this.publicNetworkId), new BridgeDomainId(this.publicNetworkId.getValue())));
        this.updateRecipe(new Bridge(bridgeName(this.serviceNetworkId), new BridgeDomainId(this.serviceNetworkId.getValue())));
        this.updateRecipe(new Bridge(bridgeName(this.transferNetworkId), new BridgeDomainId(this.transferNetworkId.getValue())));
    }
    
    protected void updateRecipe(VPPRecipe recipe)
    {
        this.vppDaemonClient.callUpdateRecipe().recipe(recipe).execute();
    }
    
    protected Set<String> getBridges()
    {
        return this.vppDaemonClient.callListRecipesOfType().type(Bridge.class).execute();
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

    public VNI getPublicNetworkId()
    {
        return publicNetworkId;
    }

    public VNI getServiceNetworkId()
    {
        return serviceNetworkId;
    }

    public VNI getTransferNetworkId()
    {
        return transferNetworkId;
    }

    public String getInterconnectAddress()
    {
        return this.interconnectCIDR.getAddress().toString();
    }
    
    protected String bridgeName(VNI vni)
    {
        return "vnet-br-" + vni.asHex();
    }
    
    protected boolean isBridge(String name)
    {
        return name.startsWith("vnet-br-");
    }
    
    protected VNI fromBridgeName(String name)
    {
        return new VNI(Integer.parseInt(name.replace("vnet-br-", ""), 16));
    }
    
    public void registerRemoteVMHost(String remoteVMHostAddress)
    {
        registerRemoteVMHosts(Collections.singleton(remoteVMHostAddress));
    }
    
    public void registerRemoteVMHosts(Set<String> remoteVMHostAddresses)
    {
        // Get all networks which are configured and add a tunnel to this host
        for (String bridge : this.getBridges())
        {
            if (isBridge(bridge))
            {
                VNI vni = fromBridgeName(bridge);
                for (String remoteVMHostAddress : remoteVMHostAddresses)
                {
                    VXLANTunnel tunnel = new VXLANTunnel(this.interconnectCIDR.getAddress(), IPv4Address.fromString(remoteVMHostAddress), vni);
                    this.updateRecipe(tunnel);
                    this.updateRecipe(new BridgeInterface(bridge, tunnel.getName(), SplitHorizonGroup.ONE));
                }
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
        // Set up the network bridge
        VNI vni = new VNI(net.getVxlanid());
        Bridge bridge = new Bridge(bridgeName(vni), new BridgeDomainId(net.getVxlanid()));
        this.updateRecipe(bridge);
        // Create the VXLAN tunnels we need
        for (HostState remoteHost : this.hostManagerContext.getActiveHostsInZone())
        {
            IPv4Address remote = IPv4Address.fromString(remoteHost.getInterconnectAddress());
            if (! this.interconnectCIDR.getAddress().equals(remote))
            {
                VXLANTunnel tunnel = new VXLANTunnel(this.interconnectCIDR.getAddress(), remote, vni);
                this.updateRecipe(tunnel);
                this.updateRecipe(new BridgeInterface(bridge, tunnel, SplitHorizonGroup.ONE));
            }
        }
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
            this.setupNetwork(nic.getNetwork());
            return this.setupGuestNIC(nic, new VNI(nic.getNetwork().getVxlanid()));
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
    
    protected InterfaceInfo setupGuestNIC(MachineInterfaceEO nic, VNI network)
    {
        VethHostInterface iface = VethHostInterface.forVM(MACAddress.fromString(nic.getMac()));
        this.updateRecipe(iface);
        this.updateRecipe(new BridgeInterface(bridgeName(network), iface.getName()));
        return toInterfaceInfo(nic, iface);
    }
    
    protected InterfaceInfo toInterfaceInfo(MachineInterfaceEO nic, VPPInterfaceRecipe iface)
    {
        if (iface instanceof VhostUserInterface)
        {
            VhostUserInterface vhostUser = (VhostUserInterface) iface;
            return new VhostUserInterfaceInfo(nic.getMac(), vhostUser.getSocket(), vhostUser.getMode() == VhostUserMode.CLIENT);
        }
        else if (iface instanceof VethHostInterface)
        {
            VethHostInterface veth = (VethHostInterface) iface;
            return new DirectInterfaceInfo(nic.getMac(), veth.getHostInterfacePeerName(), MacVTapMode.PRIVATE);
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
