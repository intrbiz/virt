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
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

/**
 * Create an AF_PACKET based host interface
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("interface.host")
public class HostInterface extends VPPRecipe implements VPPInterfaceRecipe
{
    @JsonProperty("host_interface_name")
    private String hostInterfaceName;

    @JsonProperty("mac")
    private MACAddress macAddress;
    
    @JsonProperty("mtu")
    private MTU mtu;
    
    @JsonProperty("tag")
    private Tag tag;

    private transient InterfaceIndex currentInterfaceIndex;
    
    private transient String vppInterfaceName;

    public HostInterface(String name, String hostInterfaceName, MACAddress macAddress, MTU mtu, Tag tag)
    {
        super(name);
        this.setHostInterfaceName(Objects.requireNonNull(hostInterfaceName));
        this.setMtu(Objects.requireNonNull(mtu));
        this.setMacAddress(macAddress);
        this.setTag(tag);
    }
    
    public HostInterface(String name, String hostInterfaceName, MTU mtu, Tag tag)
    {
        this(name, hostInterfaceName, null, mtu, tag);
    }
    
    public HostInterface(String name, String hostInterfaceName, Tag tag)
    {
        this(name, hostInterfaceName, null, MTU.DEFAULT, tag);
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
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        if (this.macAddress == null) this.macAddress = this.computeInterfaceMACAddress();
        this.setupHostInterface();
        this.currentInterfaceIndex = this.findExistingInterface(session);
        if (this.currentInterfaceIndex == null) this.currentInterfaceIndex = this.createInterface(session);
        this.setupInterface(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
    
    public static HostInterface forVM(String name, MACAddress vmMACAddress)
    {
        return new HostInterface(name, "vm-" + vmMACAddress.toCompactString(), new Tag("vm-" + vmMACAddress.toString()));
    }
}
