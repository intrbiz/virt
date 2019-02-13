package com.intrbiz.vpp.recipe;

import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vm.metadata")
public class VMMetadata implements VPPRecipe
{   
    public static final BridgeDomainId METADATA_BRIDGE_ID = new BridgeDomainId(10);

    @JsonProperty("metadata_bridge")
    private VMMetadataBridge<HostInterface> metadataBridge;
    
    @JsonProperty("default_interface_mode")
    private VMInterfaceType defaultInterfaceMode;

    public VMMetadata() 
    {
        super();
    }
    
    public VMMetadata(VMMetadataBridge<HostInterface> metadataBridge, VMInterfaceType defaultInterfaceMode)
    {
        super();
        this.metadataBridge = metadataBridge;
        this.defaultInterfaceMode = defaultInterfaceMode;
    }

    public VMMetadata(String metadataInterfaceName, IPv4CIDR interconnectAddress, VMInterfaceType defaultInterfaceMode)
    {
        this.metadataBridge = new VMMetadataBridge<HostInterface>(METADATA_BRIDGE_ID, new HostInterface(metadataInterfaceName, new Tag("metadata")), SplitHorizonGroup.DEFAULT);
        this.defaultInterfaceMode = defaultInterfaceMode == null ? VMInterfaceType.VETH : defaultInterfaceMode;
    }

    public VMMetadataBridge<HostInterface> getMetadataBridge()
    {
        return metadataBridge;
    }
    
    public VMInterfaceType getDefaultInterfaceMode()
    {
        return defaultInterfaceMode;
    }

    public void addVM(MACAddress metadataInterface)
    {
        this.addVM(null, metadataInterface);
    }
    
    public void addVM(VMInterfaceType type, MACAddress metadataInterface)
    {
        this.addVMMetadataInterface(type, metadataInterface);
    }
    
    public VMInterface addVMMetadataInterface(MACAddress metadataInterface)
    {
        return this.addVMMetadataInterface(null, metadataInterface);
    }
    
    public VMInterface addVMMetadataInterface(VMInterfaceType type, MACAddress metadataInterface)
    {
        VMInterface iface = (type == null ? this.defaultInterfaceMode : type).createVMInterface(metadataInterface, this.metadataBridge.getId());
        this.metadataBridge.addVM(iface);
        return iface;
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        this.metadataBridge.apply(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
