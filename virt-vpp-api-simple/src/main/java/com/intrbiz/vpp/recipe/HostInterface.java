package com.intrbiz.vpp.recipe;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.system.net.NetException;
import com.intrbiz.system.net.NetManager;
import com.intrbiz.system.sysfs.SysFs;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipeBase;
import com.intrbiz.vpp.api.recipe.VPPRecipeContext;
import com.intrbiz.vpp.util.RecipeWriter;

/**
 * Create an AF_PACKET based host interface
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("interface.host")
public class HostInterface extends VPPRecipeBase implements VPPInterfaceRecipe
{
    @JsonProperty("host_interface_name")
    private String hostInterfaceName;

    @JsonProperty("mac")
    private MACAddress macAddress;
    
    @JsonProperty("mtu")
    private MTU mtu;
    
    @JsonProperty("tag")
    private Tag tag;
    
    @JsonProperty("ip")
    private IPv4CIDR ipv4address;

    private transient InterfaceIndex currentInterfaceIndex;
    
    private transient String vppInterfaceName;

    public HostInterface(String name, String hostInterfaceName, MACAddress macAddress, MTU mtu)
    {
        super(name);
        this.setHostInterfaceName(Objects.requireNonNull(hostInterfaceName));
        this.setMtu(Objects.requireNonNull(mtu));
        this.setMacAddress(macAddress);
        this.setTag(new Tag(name));
    }
    
    public HostInterface(String name, String hostInterfaceName, MTU mtu)
    {
        this(name, hostInterfaceName, null, mtu);
    }
    
    public HostInterface(String name, String hostInterfaceName, MACAddress macAddress, MTU mtu, IPv4CIDR address)
    {
        this(name, hostInterfaceName, macAddress, mtu);
        this.ipv4address = address;
    }
    
    public HostInterface(String name, String hostInterfaceName, MTU mtu, IPv4CIDR address)
    {
        this(name, hostInterfaceName, null, mtu, address);
    }
    
    public HostInterface(String name, String hostInterfaceName)
    {
        this(name, hostInterfaceName, null, MTU.DEFAULT);
    }
    
    public HostInterface(String hostInterfaceName, MACAddress macAddress, MTU mtu, Tag tag)
    {
        super("host-" + hostInterfaceName);
        this.setHostInterfaceName(Objects.requireNonNull(hostInterfaceName));
        this.setMtu(Objects.requireNonNull(mtu));
        this.setMacAddress(macAddress);
        this.setTag(tag);
    }
    
    public HostInterface(String hostInterfaceName, MTU mtu, Tag tag)
    {
        this(hostInterfaceName, null, mtu, tag);
    }
    
    public HostInterface(String hostInterfaceName, Tag tag)
    {
        this(hostInterfaceName, null, MTU.DEFAULT, tag);
    }
    
    public HostInterface(String hostInterfaceName)
    {
        this(hostInterfaceName, null, MTU.DEFAULT, new Tag(hostInterfaceName));
    }
    
    public HostInterface()
    {
        super();
    }
    
    public String getHostInterfaceName()
    {
        return this.hostInterfaceName;
    }

    public MACAddress getMacAddress()
    {
        return macAddress;
    }

    public MTU getMtu()
    {
        return mtu;
    }

    public Tag getTag()
    {
        return tag;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    @Override
    @JsonIgnore
    public InterfaceIndex getCurrentInterfaceIndex()
    {
        return currentInterfaceIndex;
    }

    @JsonIgnore
    public String getVppInterfaceName()
    {
        return vppInterfaceName;
    }

    public void setHostInterfaceName(String hostInterfaceName)
    {
        this.hostInterfaceName = hostInterfaceName;
        this.vppInterfaceName = "host-" + this.hostInterfaceName;
    }

    public void setMacAddress(MACAddress macAddress)
    {
        this.macAddress = macAddress;
    }

    public void setMtu(MTU mtu)
    {
        this.mtu = mtu;
    }
    
    public IPv4CIDR getIpv4address()
    {
        return ipv4address;
    }

    public void setIpv4address(IPv4CIDR ipv4address)
    {
        this.ipv4address = ipv4address;
    }

    protected InterfaceIndex findExistingInterface(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (InterfaceDetail iface : session.interfaces().listInterfaces().get())
        {
            if (this.vppInterfaceName.equals(iface.getName()))
                return iface.getIndex();
        }
        return null;
    }

    protected InterfaceIndex createInterface(VPPSimple session) throws InterruptedException, ExecutionException
    {
        System.out.println("Creating host interface: " + this.hostInterfaceName);
        InterfaceIndex iface = session.interfaces().createHostInterface(this.hostInterfaceName, this.macAddress).get();
        session.interfaces().setInterfaceTag(iface, this.tag);
        return iface;
    }

    protected void setupInterface(VPPSimple session) throws InterruptedException, ExecutionException
    {
        System.out.println("Configuring host interface: " + this.hostInterfaceName);
        session.interfaces().setInterfaceMTU(this.currentInterfaceIndex, this.mtu);
        if (this.ipv4address != null)
        {
            session.interfaces().addInterfaceIPv4Address(this.currentInterfaceIndex, this.ipv4address);
        }
        session.interfaces().setInterfaceUp(this.currentInterfaceIndex);
    }
    
    protected MACAddress computeInterfaceMACAddress()
    {
        MACAddress mac = SysFs.sysFs().getInterfaceMAC(this.hostInterfaceName);
        return mac == null ? MACAddress.random() : mac;
    }
    
    protected void setupHostInterface()
    {
        NetManager netMan = NetManager.getNetManager();
        SysFs sysFs = SysFs.sysFs();
        // Ensure the host interface is created
        if (! sysFs.interfaceExists(this.hostInterfaceName)) createHostInterface(sysFs, netMan);
        // Set host interface MTU
        MTU hostInterfaceMTU = sysFs.getInterfaceMTU(this.hostInterfaceName);
        if (hostInterfaceMTU == null || (! hostInterfaceMTU.equals(this.mtu)))
        {
            netMan.setMTU(this.hostInterfaceName, this.mtu);
        }
        // Set host interface promiscuous if we are not using the interface MAC
        MACAddress hostInterfaceMAC = sysFs.getInterfaceMAC(this.hostInterfaceName);
        if (hostInterfaceMAC != null && (! hostInterfaceMAC.equals(this.macAddress)))
        {
            netMan.promiscuousMode(this.hostInterfaceName, true);
        }
        // Ensure the interface is up
        boolean hostInterfaceUp = sysFs.isInterfaceUp(this.hostInterfaceName);
        if (! hostInterfaceUp)
        {
            netMan.setUp(this.hostInterfaceName);
        }
    }
    
    protected void createHostInterface(SysFs sysFs, NetManager netMan)
    {
        // To be implemented by subclasses which know how to create the underlying host interface
        throw new NetException("Host interface " + this.hostInterfaceName + " does not exist, cannot create it");
    }
    
    @Override
    public void apply(VPPSimple session, VPPRecipeContext context) throws InterruptedException, ExecutionException
    {
        if (this.macAddress == null) this.macAddress = this.computeInterfaceMACAddress();
        this.setupHostInterface();
        this.currentInterfaceIndex = this.findExistingInterface(session);
        if (this.currentInterfaceIndex == null) this.currentInterfaceIndex = this.createInterface(session);
        this.setupInterface(session);
    }
    
    @Override
    public void unapply(VPPSimple session, VPPRecipeContext context) throws InterruptedException, ExecutionException
    {
        // TODO
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
    
    public static HostInterface forVM(MACAddress vmMACAddress)
    {
        return new HostInterface("vm-" + vmMACAddress.toCompactString(), new Tag("vm-" + vmMACAddress.toString()));
    }
}
