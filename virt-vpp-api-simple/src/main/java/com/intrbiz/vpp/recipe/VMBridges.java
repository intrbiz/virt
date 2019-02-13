package com.intrbiz.vpp.recipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.recipe.VPPBridgesRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vm.bridges")
public class VMBridges<T extends VPPBridgesRecipe> implements VPPRecipe
{
    @JsonProperty("bridges")
    @JsonSubTypes({@Type(VPPBridgesRecipe.class)})
    private T bridges;
    
    @JsonProperty("vms")
    @JsonSubTypes({@Type(VMInterface.class)})
    private final Set<VMInterface> vms = new HashSet<VMInterface>();
    
    private final transient Map<MACAddress, VMInterface> vmsIndex = new HashMap<MACAddress, VMInterface>();
    
    public VMBridges()
    {
        super();
    }
    
    public VMBridges(T bridges, Set<VMInterface> vms)
    {
        this.bridges = Objects.requireNonNull(bridges);
        this.setVms(vms);
    }
    
    public VMBridges(T bridges)
    {
        this(bridges, null);
    }

    public T getBridges()
    {
        return bridges;
    }

    public void setBridges(T bridges)
    {
        this.bridges = bridges;
    }

    public Set<VMInterface> getVms()
    {
        return Collections.unmodifiableSet(this.vms);
    }
    
    public void setVms(Set<VMInterface> vms)
    {
        this.vms.clear();
        this.vmsIndex.clear();
        if (vms != null)
        {
            for (VMInterface vm : vms)
            {
                this.vms.add(vm);
                this.vmsIndex.putIfAbsent(vm.getVmMACAddress(), vm);
            }
        }
    }
    
    public VMInterface getVM(MACAddress mac)
    {
        return this.vmsIndex.get(mac);
    }
    
    public void addVM(VMInterface vm)
    {
        this.vms.add(vm);
        this.vmsIndex.putIfAbsent(vm.getVmMACAddress(), vm);
    }
    
    protected void addVMsToBridges(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (VMInterface vm : this.vms)
        {
            Bridge bridge = this.bridges.getBridge(vm.getBridgeId());
            // ensure the interface is created
            vm.apply(session);
            // add the interface to the bridge
            System.out.println("Adding interface " + vm.getCurrentInterfaceIndex() + " to bridge " + bridge.getId());
            session.bridge().addInterfaceToBridgeDomain(bridge.getId(), vm.getCurrentInterfaceIndex());
        }
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        // ensure the bridges are created
        this.bridges.apply(session);
        // add the vms to the bridges
        this.addVMsToBridges(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
