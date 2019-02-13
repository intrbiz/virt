package com.intrbiz.vpp.recipe;

import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vm.networks")
public class VMNetworks implements VPPRecipe
{
    @JsonProperty("interconnect")
    private VMInterconnect<HostInterface> interconnect;

    @JsonProperty("vm_bridges")
    private VMBridges<VXLANMesh> vmBridges;
    
    @JsonProperty("default_interface_mode")
    private VMInterfaceType defaultInterfaceMode;

    public VMNetworks()
    {
        super();
    }
    
    public VMNetworks(VMInterconnect<HostInterface> interconnect, VMBridges<VXLANMesh> vmBridges, VMInterfaceType defaultInterfaceMode)
    {
        super();
        this.interconnect = interconnect;
        this.vmBridges = vmBridges;
        this.defaultInterfaceMode = defaultInterfaceMode;
    }

    public VMNetworks(String interconnectInterfaceName, IPv4CIDR interconnectAddress, VMInterfaceType defaultInterfaceMode)
    {
        this.interconnect = new VMInterconnect<HostInterface>(new HostInterface(interconnectInterfaceName, MTU.JUMBO, new Tag("interconnect")), interconnectAddress);
        this.vmBridges = new VMBridges<VXLANMesh>(new VXLANMesh(interconnectAddress.getAddress()));
        this.defaultInterfaceMode = defaultInterfaceMode == null ? VMInterfaceType.VETH : defaultInterfaceMode;
    }

    public VMInterconnect<HostInterface> getInterconnect()
    {
        return interconnect;
    }

    public VMBridges<VXLANMesh> getVmBridges()
    {
        return vmBridges;
    }
    
    public VMInterfaceType getDefaultInterfaceMode()
    {
        return defaultInterfaceMode;
    }

    public void addNetwork(VMNetworkId network)
    {
        this.vmBridges.getBridges().addNetwork(network);
    }
    
    public void addRemoteVMHost(IPv4Address remoteVMHost)
    {
        this.vmBridges.getBridges().addDestination(remoteVMHost);
    }
    
    public void addVM(MACAddress mainInterface, VMNetworkId mainNetwork)
    {
        this.addVM(null, mainInterface, mainNetwork);
    }
    
    public void addVM(VMInterfaceType type, MACAddress mainInterface, VMNetworkId mainNetwork)
    {
        this.addVMInterface(type, mainInterface, mainNetwork);
    }
    
    public VMInterface addVMInterface(MACAddress mainInterface, VMNetworkId network)
    {
        return addVMInterface(null, mainInterface, network);
    }
    
    public VMInterface addVMInterface(VMInterfaceType type, MACAddress mainInterface, VMNetworkId network)
    {
        // Ensure the network exists
        this.addNetwork(network);
        // Add the interface
        VMInterface iface = (type == null ? this.defaultInterfaceMode : type).createVMInterface(mainInterface, network.toBridgeDomain());
        this.vmBridges.addVM(iface);
        return iface;
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        this.interconnect.apply(session);
        this.vmBridges.apply(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
