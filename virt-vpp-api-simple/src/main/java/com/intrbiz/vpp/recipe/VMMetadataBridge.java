package com.intrbiz.vpp.recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.recipe.VPPBridgeRecipe;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPInterfacesRecipe;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vm.metadata.bridge")
public class VMMetadataBridge<T extends VPPInterfaceRecipe> implements VPPBridgeRecipe, VPPInterfaceRecipe, VPPInterfacesRecipe
{
    @JsonProperty("metadata_bridge")
    private Bridge metadataBridge;

    @JsonProperty("metadata_interface")
    private T metadataInterface;

    @JsonProperty("vms")
    private Set<VMInterface> vms = new HashSet<VMInterface>();
    
    @JsonProperty("vm_split_horizon_group")
    private SplitHorizonGroup vmSplitHorizonGroup;

    public VMMetadataBridge()
    {
        super();
    }
    
    public VMMetadataBridge(BridgeDomainId metadataBridgeId, T metadataInterface, Set<VMInterface> vms, SplitHorizonGroup vmSplitHorizonGroup)
    {
        Objects.requireNonNull(metadataBridgeId);
        this.metadataBridge = new Bridge(metadataBridgeId, 60, new Tag("metadata"));
        this.metadataInterface = Objects.requireNonNull(metadataInterface);
        this.vmSplitHorizonGroup = vmSplitHorizonGroup;
        if (vms != null) this.vms.addAll(vms);
    }
    
    public VMMetadataBridge(BridgeDomainId metadataBridgeId, T metadataInterface, SplitHorizonGroup vmSplitHorizonGroup)
    {
        this(metadataBridgeId, metadataInterface, null, vmSplitHorizonGroup);
    }

    public Bridge getMetadataBridge()
    {
        return metadataBridge;
    }

    public T getMetadataInterface()
    {
        return metadataInterface;
    }

    public Set<VMInterface> getVms()
    {
        return Collections.unmodifiableSet(vms);
    }

    public void addVM(VMInterface vm)
    {
        this.vms.add(vm);
    }

    public void setMetadataBridge(Bridge metadataBridge)
    {
        this.metadataBridge = metadataBridge;
    }

    public void setMetadataInterface(T metadataInterface)
    {
        this.metadataInterface = metadataInterface;
    }

    public void setVms(Set<VMInterface> vms)
    {
        this.vms = vms;
    }

    public void setVmSplitHorizonGroup(SplitHorizonGroup vmSplitHorizonGroup)
    {
        this.vmSplitHorizonGroup = vmSplitHorizonGroup;
    }

    @Override
    @JsonIgnore
    public InterfaceIndex getCurrentInterfaceIndex()
    {
        return this.metadataInterface.getCurrentInterfaceIndex();
    }

    @Override
    @JsonIgnore
    public BridgeDomainId getId()
    {
        return this.metadataBridge.getId();
    }

    @Override
    @JsonIgnore
    public Collection<? extends VPPInterfaceRecipe> getInterfaces()
    {
        return Collections.unmodifiableSet(this.vms);
    }
    
    protected void addVMsToMetadataBridge(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (VMInterface vm : this.vms)
        {
            // ensure the interface is created
            vm.apply(session);
            // add the interface to the bridge
            System.out.println("Adding interface " + vm.getCurrentInterfaceIndex() + " to bridge " + this.metadataBridge.getId());
            session.bridge().addInterfaceToBridgeDomain(this.metadataBridge.getId(), vm.getCurrentInterfaceIndex(), this.vmSplitHorizonGroup);
        }
    }
    
    protected void addMetadataInterfaceToMetadataBridge(VPPSimple session) throws InterruptedException, ExecutionException
    {
        session.bridge().addInterfaceToBridgeDomain(this.metadataBridge.getId(), this.metadataInterface.getCurrentInterfaceIndex());
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        // create the metadata bridge
        this.metadataBridge.apply(session);
        // create the host interface
        this.metadataInterface.apply(session);
        // add the metadata interface to the metadata bridge
        this.addMetadataInterfaceToMetadataBridge(session);
        // add the vms to the bridges
        this.addVMsToMetadataBridge(session);
    }
}
