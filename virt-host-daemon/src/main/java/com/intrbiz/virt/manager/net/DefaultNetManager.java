package com.intrbiz.virt.manager.net;

import static com.intrbiz.system.exec.Command.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.intrbiz.system.exec.SystemExecutorService;
import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.config.NetManagerCfg;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineInterfaceEO;
import com.intrbiz.virt.event.model.NetworkEO;
import com.intrbiz.virt.manager.HostManagerContext;
import com.intrbiz.virt.manager.HostMetadataStoreContext;
import com.intrbiz.virt.manager.net.model.BridgedInterfaceInfo;
import com.intrbiz.virt.manager.net.model.InterfaceInfo;
import com.intrbiz.virt.util.IDUtil;

public class DefaultNetManager implements NetManager
{
    private static final Logger logger = Logger.getLogger(DefaultNetManager.class);
    
    private final Set<String> supportedTypes = Collections.unmodifiableSet(new TreeSet<String>(Arrays.asList("vxlan", "public", "transfer", "service")));
    
    private String publicBridge;
    
    private String transferBridge;
    
    private String serviceBridge;
    
    private String vxlanInterface;
    
    private String vxlanAddress;
    
    private int vxlanPort;
    
    private int vxlanMtu;
    
    private String vxlanPrefix;
    
    private String bridgePrefix;
    
    private String metadataBridge;
    
    private String interconnectAddress;
    
    private NetManagerCfg config;
    
    private final SystemExecutorService executor = SystemExecutorService.getSystemExecutorService();
    
    public DefaultNetManager()
    {
        super();
    }
    
    @Override
    public void configure(NetManagerCfg cfg) throws Exception
    {
        this.config = cfg;
        this.publicBridge = cfg.getStringParameterValue("public.bridge.name", "br20");
        this.transferBridge = cfg.getStringParameterValue("transfer.bridge.name", "br80");
        this.serviceBridge = cfg.getStringParameterValue("transfer.bridge.name", "br40");
        this.vxlanInterface = cfg.getStringParameterValue("vxlan.parent.interface.name", "wlp4s0");
        this.vxlanAddress = cfg.getStringParameterValue("vxlan.group.address", "239.1.1.1");
        this.vxlanPort = cfg.getIntParameterValue("vxlan.port", 4789);
        this.vxlanMtu = cfg.getIntParameterValue("vxlan.mtu", 1500);
        this.vxlanPrefix = cfg.getStringParameterValue("vxlan.tunnel.interface.prefix", "vx");
        this.bridgePrefix = cfg.getStringParameterValue("vxlan.bridge.prefix", "br");
        this.metadataBridge = cfg.getStringParameterValue("metadata.bridge.name", "brcfg");
        this.interconnectAddress = cfg.getStringParameterValue("host.interconnect.address", "127.0.0.1");
    }

    @Override
    public NetManagerCfg getConfiguration()
    {
        return this.config;
    }

    public void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext)
    {
    }
    
    public String getInterconnectAddress()
    {
        return interconnectAddress;
    }
    
    public void registerRemoteVMHost(String remoteVMHostAddress)
    {
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
    
    /**
     * Create the given guest metadata NIC on this host 
     */
    public synchronized InterfaceInfo setupGuestMetadataNIC(MachineEO machine)
    {
        return new BridgedInterfaceInfo(machine.getCfgMac(), this.metadataBridge);
    }
    
    /**
     * Create the given guest NIC on this host
     */
    public synchronized InterfaceInfo setupGuestNIC(MachineInterfaceEO nic)
    {
        return new BridgedInterfaceInfo(nic.getMac(), this.doSetupNetwork(nic.getNetwork()));
    }
    
    @Override
    public void setupNetwork(NetworkEO net)
    {
        this.doSetupNetwork(net);
    }
    
    public synchronized String doSetupNetwork(NetworkEO net)
    {
        if ("vxlan".equals(net.getType()))
        {
            this.setupVxlanNetwork(net);
        }
        else if ("public".equalsIgnoreCase(net.getType()))
        {
            this.setupPublicNetwork(net);
        }
        else if ("transfer".equalsIgnoreCase(net.getType()))
        {
            this.setupTransferNetwork(net);
        }
        else if ("service".equalsIgnoreCase(net.getType()))
        {
            this.setupServiceNetwork(net);
        }
        throw new VirtError("Cannot create network of type: " + net.getType());
    }
    
    protected String setupPublicNetwork(NetworkEO net)
    {
        return this.publicBridge;
    }
    
    protected String setupTransferNetwork(NetworkEO net)
    {
        return this.transferBridge;
    }
    
    protected String setupServiceNetwork(NetworkEO net)
    {
        return this.serviceBridge;
    }
    
    protected String setupVxlanNetwork(NetworkEO net)
    {
        String vxlan = this.setupVxlanInterface(net.getVxlanid());
        String bridge = this.setupVxlanBridge(net.getVxlanid());
        this.addInterfaceToBridge(bridge, vxlan);
        this.updateFirewall(vxlan, bridge);
        return bridge;
    }
    
    protected String setupVxlanInterface(int vxlanId)
    {
        String ifName = IDUtil.vxlanHex(this.vxlanPrefix, vxlanId);
        logger.info("Creating vxlan interface: " + ifName + " with id " + vxlanId + " on interface " + this.vxlanInterface);
        // ip link add vxlan{{ network.id }} type vxlan id {{ network.id }} group 239.1.1.1 dev vlan40 dstport 4789
        this.executor.fireAndForget(command("/usr/sbin/ip", "link", "add", ifName, "type", "vxlan", "id", String.valueOf(vxlanId), "group", this.vxlanAddress, "dev", this.vxlanInterface, "dstport", String.valueOf(this.vxlanPort)));
        // ip link set mtu 1500 dev vxlan{{ network.id }}
        this.executor.fireAndForget(command("/usr/sbin/ip", "link", "set", "mtu", String.valueOf(this.vxlanMtu), "dev", ifName));
        // ip link set up vxlan{{ network.id }}
        this.executor.fireAndForget(command("/usr/sbin/ip", "link", "set", "up", ifName));
        return ifName;
    }
    
    protected String setupVxlanBridge(int vxlanId)
    {
        String brName = IDUtil.vxlanHex(this.bridgePrefix, vxlanId);
        logger.info("Creating bridge: " + brName);
        // brctl addbr br{{ network.id }}
        this.executor.fireAndForget(command("/usr/sbin/brctl", "addbr", brName));
        // ip link set up br{{ network.id }}
        this.executor.fireAndForget(command("/usr/sbin/ip", "link", "set", "up", brName));
        return brName;
    }
    
    protected void addInterfaceToBridge(String brName, String ifName)
    {
        logger.info("Adding interface " + ifName + " into bridge " + brName);
        // brctl addif br{{ network.id }} vxlan{{ network.id }}
        this.executor.fireAndForget(command("/usr/sbin/brctl", "addbr", brName, ifName));
    }
    
    protected void updateFirewall(String vxlan, String bridge)
    {
    }

    @Override
    public void startGuestMetadataNIC(MachineEO machine)
    {
    }

    @Override
    public void startGuestNIC(MachineInterfaceEO nic)
    {
    }

    @Override
    public void stopGuestMetadataNIC(MachineEO machine)
    {
        
    }

    @Override
    public void stopGuestNIC(MachineInterfaceEO nic)
    {   
    }

    @Override
    public void removeGuestMetadataNIC(MachineEO machine)
    {
    }

    @Override
    public void removeGuestNIC(MachineInterfaceEO nic)
    {
    }
    
    @Override
    public void releaseGuestMetadataNIC(MachineEO machine)
    {
    }

    @Override
    public void releaseGuestNIC(MachineInterfaceEO nic)
    {
    }
}
