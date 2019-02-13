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
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

/**
 * Create an interface used to interconnect VMs
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vm.interconnect")
public class VMInterconnect<T extends VPPInterfaceRecipe> implements VPPInterfaceRecipe
{
    @JsonProperty("interface")
    private T interconnectInterface;
    
    @JsonProperty("ip")
    private IPv4CIDR ipv4address;

    public VMInterconnect(T interconnectInterface, IPv4CIDR ipv4address)
    {
        this.interconnectInterface = interconnectInterface;
        this.ipv4address = Objects.requireNonNull(ipv4address);
    }
    
    public VMInterconnect()
    {
        super();
    }

    public T getInterconnectInterface()
    {
        return this.interconnectInterface;
    }
    
    public IPv4CIDR getIpv4address()
    {
        return ipv4address;
    }

    public void setInterconnectInterface(T interconnectInterface)
    {
        this.interconnectInterface = interconnectInterface;
    }

    public void setIpv4address(IPv4CIDR ipv4address)
    {
        this.ipv4address = ipv4address;
    }

    @Override
    @JsonIgnore
    public InterfaceIndex getCurrentInterfaceIndex()
    {
        return this.interconnectInterface.getCurrentInterfaceIndex();
    }

    private void setupInterface(VPPSimple session) throws InterruptedException, ExecutionException
    {
        session.interfaces().addInterfaceIPv4Address(this.interconnectInterface.getCurrentInterfaceIndex(), this.ipv4address);
        session.vxlan().setIPv4VXLANBypass(this.interconnectInterface.getCurrentInterfaceIndex(), true);
    }
    
    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        this.interconnectInterface.apply(session);
        this.setupInterface(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
