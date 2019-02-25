package com.intrbiz.vpp.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.system.net.NetManager;
import com.intrbiz.system.sysfs.SysFs;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("interface.host.veth")
public class VethHostInterface extends HostInterface
{
    @JsonProperty("host_interface_peer_name")
    private String hostInterfacePeerName;

    public VethHostInterface()
    {
        super();
    }

    public VethHostInterface(String hostInterfaceName, String hostInterfacePeerName, MACAddress macAddress, MTU mtu, Tag tag)
    {
        super(hostInterfaceName, macAddress, mtu, tag);
        this.hostInterfacePeerName = hostInterfacePeerName;
    }

    public VethHostInterface(String hostInterfaceName, String hostInterfacePeerName, MTU mtu, Tag tag)
    {
        this(hostInterfaceName, hostInterfacePeerName, null, mtu, tag);
    }

    public VethHostInterface(String hostInterfaceName, String hostInterfacePeerName, Tag tag)
    {
        this(hostInterfaceName, hostInterfacePeerName, null, MTU.DEFAULT, tag);
    }

    public String getHostInterfacePeerName()
    {
        return hostInterfacePeerName;
    }

    public void setHostInterfacePeerName(String hostInterfacePeerName)
    {
        this.hostInterfacePeerName = hostInterfacePeerName;
    }

    @Override
    protected void createHostInterface(SysFs sysFs, NetManager netMan)
    {
        // Create the veth interface
        netMan.createVeth(this.getHostInterfaceName(), this.getHostInterfacePeerName());
    }
    
    public static VethHostInterface forVM(MACAddress vmMACAddress)
    {
        return new VethHostInterface("vm-" + vmMACAddress.toCompactString(), "vn-" + vmMACAddress.toCompactString(), MACAddress.random(), MTU.DEFAULT, new Tag("vm-" + vmMACAddress.toString()));
    }
}
