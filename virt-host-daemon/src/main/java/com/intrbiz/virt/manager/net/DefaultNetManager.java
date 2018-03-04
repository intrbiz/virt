package com.intrbiz.virt.manager.net;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.event.model.NetworkEO;
import com.intrbiz.virt.util.ExecUtil;
import com.intrbiz.virt.util.IDUtil;

public class DefaultNetManager implements NetManager
{
    private static final Logger logger = Logger.getLogger(DefaultNetManager.class);
    
    private String publicBridge = "br20";
    
    private String transferBridge = "br80";
    
    private String serviceBridge = "br40";
    
    private String vxlanInterface = "wlp4s0";
    
    private String vxlanAddress = "239.1.1.1";
    
    private int vxlanPort = 4789;
    
    private int vxlanMtu = 1500;
    
    private String vxlanPrefix = "vx";
    
    private String bridgePrefix = "br";
    
    public DefaultNetManager()
    {
        super();
    }
    
    public void start()
    {
    }
    
    @Override
    public Set<String> getSupportedNetworkTypes()
    {
        return new TreeSet<String>(Arrays.asList("vxlan", "public", "transfer", "service"));
    }
    
    @Override
    public boolean isSupported(NetworkEO net)
    {
        return "vxlan".equals(net.getType()) || "public".equals(net.getType()) || "transfer".equals(net.getType()) || "service".equals(net.getType());
    }
    
    @Override
    public synchronized String setupNetwork(NetworkEO net)
    {
        if ("vxlan".equals(net.getType()))
        {
            return this.setupVxlanNetwork(net);
        }
        else if ("public".equalsIgnoreCase(net.getType()))
        {
            return this.setupPublicNetwork(net);
        }
        else if ("transfer".equalsIgnoreCase(net.getType()))
        {
            return this.setupTransferNetwork(net);
        }
        else if ("service".equalsIgnoreCase(net.getType()))
        {
            return this.setupServiceNetwork(net);
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
        ExecUtil.assume("/usr/sbin/ip", Arrays.asList("link", "add", ifName, "type", "vxlan", "id", String.valueOf(vxlanId), "group", this.vxlanAddress, "dev", this.vxlanInterface, "dstport", String.valueOf(this.vxlanPort)));
        // ip link set mtu 1500 dev vxlan{{ network.id }}
        ExecUtil.assume("/usr/sbin/ip", Arrays.asList("link", "set", "mtu", String.valueOf(this.vxlanMtu), "dev", ifName));
        // ip link set up vxlan{{ network.id }}
        ExecUtil.assume("/usr/sbin/ip", Arrays.asList("link", "set", "up", ifName));
        return ifName;
    }
    
    protected String setupVxlanBridge(int vxlanId)
    {
        String brName = IDUtil.vxlanHex(this.bridgePrefix, vxlanId);
        logger.info("Creating bridge: " + brName);
        // brctl addbr br{{ network.id }}
        ExecUtil.assume("/usr/sbin/brctl", Arrays.asList("addbr", brName));
        // ip link set up br{{ network.id }}
        ExecUtil.assume("/usr/sbin/ip", Arrays.asList("link", "set", "up", brName));
        return brName;
    }
    
    protected void addInterfaceToBridge(String brName, String ifName)
    {
        logger.info("Adding interface " + ifName + " into bridge " + brName);
        // brctl addif br{{ network.id }} vxlan{{ network.id }}
        ExecUtil.assume("/usr/sbin/brctl", Arrays.asList("addbr", brName, ifName));
    }
    
    protected void updateFirewall(String vxlan, String bridge)
    {
    }
}
