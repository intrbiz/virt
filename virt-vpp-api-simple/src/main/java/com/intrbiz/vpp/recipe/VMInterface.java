package com.intrbiz.vpp.recipe;

import java.util.Objects;
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
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

/**
 * Create an interface to a VM
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vm.interface")
public class VMInterface implements VPPInterfaceRecipe
{
    
    @JsonProperty("interface")
    private VPPInterfaceRecipe vmInterface;

    @JsonProperty("mac")
    private MACAddress vmMACAddress;

    @JsonProperty("bridge")
    private BridgeDomainId bridgeId;

    public VMInterface(MACAddress vmMACAddress, BridgeDomainId bridgeId, VPPInterfaceRecipe vmInterface)
    {
        this.vmMACAddress = Objects.requireNonNull(vmMACAddress);
        this.bridgeId = Objects.requireNonNull(bridgeId);
        this.vmInterface = Objects.requireNonNull(vmInterface);
    }
    
    public VMInterface()
    {
        super();
    }

    public MACAddress getVmMACAddress()
    {
        return vmMACAddress;
    }

    public BridgeDomainId getBridgeId()
    {
        return bridgeId;
    }

    public VPPInterfaceRecipe getVmInterface()
    {
        return vmInterface;
    }

    public void setVmInterface(VPPInterfaceRecipe vmInterface)
    {
        this.vmInterface = vmInterface;
    }

    public void setVmMACAddress(MACAddress vmMACAddress)
    {
        this.vmMACAddress = vmMACAddress;
    }

    public void setBridgeId(BridgeDomainId bridgeId)
    {
        this.bridgeId = bridgeId;
    }

    @Override
    @JsonIgnore
    public InterfaceIndex getCurrentInterfaceIndex()
    {
        return this.vmInterface.getCurrentInterfaceIndex();
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        this.vmInterface.apply(session);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((vmMACAddress == null) ? 0 : vmMACAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VMInterface other = (VMInterface) obj;
        if (vmMACAddress == null)
        {
            if (other.vmMACAddress != null) return false;
        }
        else if (!vmMACAddress.equals(other.vmMACAddress)) return false;
        return true;
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
    
    public static VMInterface forVMUsingVhostUserInterface(MACAddress vmMACAddress, BridgeDomainId bridgeId)
    {
        return new VMInterface(vmMACAddress, bridgeId, VhostUserInterface.forVM(vmMACAddress));
    }
    
    public static VMInterface forVMUsingVethHostInterface(MACAddress vmMACAddress, BridgeDomainId bridgeId)
    {
        return new VMInterface(vmMACAddress, bridgeId, VethHostInterface.forVM(vmMACAddress));
    }
    
    public static VMInterface forVMUsingHostInterface(MACAddress vmMACAddress, BridgeDomainId bridgeId)
    {
        return new VMInterface(vmMACAddress, bridgeId, HostInterface.forVM(vmMACAddress));
    }
}
