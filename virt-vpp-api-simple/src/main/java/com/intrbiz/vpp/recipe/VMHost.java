package com.intrbiz.vpp.recipe;

import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vm.host")
public class VMHost implements VPPRecipe
{
    public static final BridgeDomainId METADATA_BRIDGE_ID = new BridgeDomainId(10);
    
    @JsonProperty("networks")
    private VMNetworks networks;
    
    @JsonProperty("metadata")
    private VMMetadata metadata;
    
    public VMHost()
    {
        super();
    }
    
    public VMHost(VMNetworks networks, VMMetadata metadata)
    {
        super();
        this.networks = networks;
        this.metadata = metadata;
    }

    public VMHost(VMInterconnect<HostInterface> interconnect, VMMetadataBridge<HostInterface> metadataBridge, VMBridges<VXLANMesh> vmBridges, VMInterfaceType defaultInterfaceMode)
    {
        this(
            new VMNetworks(interconnect, vmBridges, defaultInterfaceMode),
            new VMMetadata(metadataBridge, defaultInterfaceMode)
        );
    }

    public VMHost(String interconnectInterfaceName, String metadataInterfaceName, IPv4CIDR interconnectAddress, VMInterfaceType defaultInterfaceMode)
    {
        this(
            new VMInterconnect<HostInterface>(new HostInterface(interconnectInterfaceName, MTU.JUMBO, new Tag("interconnect")), interconnectAddress),
            new VMMetadataBridge<HostInterface>(METADATA_BRIDGE_ID, new HostInterface(metadataInterfaceName, new Tag("metadata")), SplitHorizonGroup.DEFAULT),
            new VMBridges<VXLANMesh>(new VXLANMesh(interconnectAddress.getAddress())),
            defaultInterfaceMode == null ? VMInterfaceType.VETH : defaultInterfaceMode
        );
    }
    
    public VMNetworks getNetworks()
    {
        return networks;
    }

    public VMMetadata getMetadata()
    {
        return metadata;
    }
    
    public void addNetwork(VMNetworkId network)
    {
        this.networks.addNetwork(network);
    }
    
    public void addRemoteVMHost(IPv4Address remoteVMHost)
    {
        this.networks.addRemoteVMHost(remoteVMHost);
    }

    public void addVM(MACAddress metadataInterface, MACAddress mainInterface, VMNetworkId mainNetwork)
    {
        this.metadata.addVM(metadataInterface);
        this.networks.addVM(mainInterface, mainNetwork);
    }
    
    public void addVM(VMInterfaceType type, MACAddress metadataInterface, MACAddress mainInterface, VMNetworkId mainNetwork)
    {
        this.metadata.addVM(type, metadataInterface);
        this.networks.addVM(type, mainInterface, mainNetwork);
    }
    
    public VMInterface addVMMetadataInterface(MACAddress metadataInterface)
    {
        return this.metadata.addVMMetadataInterface(metadataInterface);
    }
    
    public VMInterface addVMMetadataInterface(VMInterfaceType type, MACAddress metadataInterface)
    {
        return this.metadata.addVMMetadataInterface(type, metadataInterface);
    }
    
    public VMInterface addVMInterface(MACAddress mainInterface, VMNetworkId network)
    {
        return this.networks.addVMInterface(mainInterface, network);
    }
    
    public VMInterface addVMInterface(VMInterfaceType type, MACAddress mainInterface, VMNetworkId network)
    {
        return this.networks.addVMInterface(type, mainInterface, network);
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        this.metadata.apply(session);
        this.networks.apply(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
