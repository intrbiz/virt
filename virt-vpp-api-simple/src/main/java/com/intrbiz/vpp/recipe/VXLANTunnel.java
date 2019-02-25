package com.intrbiz.vpp.recipe;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.model.VNI;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipeBase;
import com.intrbiz.vpp.api.recipe.VPPRecipeContext;
import com.intrbiz.vpp.util.RecipeWriter;

/**
 * Create a point to point VxLAN tunnel
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vxlan.tunnel")
public class VXLANTunnel extends VPPRecipeBase implements VPPInterfaceRecipe
{
    public static final String name(VNI vni, IPv4Address destinationAddress)
    {
        return "vxlan-" + vni.asHex() + "-to-" + destinationAddress.asHex();
    }
    
    @JsonProperty("source")
    private IPv4Address sourceAddress;

    @JsonProperty("destination")
    private IPv4Address destinationAddress;

    @JsonProperty("vni")
    private VNI vni;

    @JsonProperty("tag")
    private Tag tag;

    private transient InterfaceIndex currentInterfaceIndex;

    public VXLANTunnel(String name, IPv4Address sourceAddress, IPv4Address destinationAddress, VNI vni, Tag tag)
    {
        super(name);
        this.sourceAddress = Objects.requireNonNull(sourceAddress);
        this.destinationAddress = Objects.requireNonNull(destinationAddress);
        this.vni = Objects.requireNonNull(vni);
        this.tag = Objects.requireNonNull(tag);
    }
    
    public VXLANTunnel(String name, IPv4Address sourceAddress, IPv4Address destinationAddress, VNI vni)
    {
        this(name, sourceAddress, destinationAddress, vni, new Tag(name));
    }
    
    public VXLANTunnel(IPv4Address sourceAddress, IPv4Address destinationAddress, VNI vni)
    {
        this(name(vni, destinationAddress), sourceAddress, destinationAddress, vni);
    }

    public VXLANTunnel()
    {
        super();
    }

    public IPv4Address getSourceAddress()
    {
        return sourceAddress;
    }

    public IPv4Address getDestinationAddress()
    {
        return destinationAddress;
    }

    public VNI getVni()
    {
        return vni;
    }

    public Tag getTag()
    {
        return tag;
    }

    public void setSourceAddress(IPv4Address sourceAddress)
    {
        this.sourceAddress = sourceAddress;
    }

    public void setDestinationAddress(IPv4Address destinationAddress)
    {
        this.destinationAddress = destinationAddress;
    }

    public void setVni(VNI vni)
    {
        this.vni = vni;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    @Override
    public InterfaceIndex getCurrentInterfaceIndex()
    {
        return currentInterfaceIndex;
    }

    protected InterfaceIndex findExistingVXLANTunnel(VPPSimple session) throws InterruptedException, ExecutionException
    {
        // TODO: we can probably better match rather than using just the tag
        for (InterfaceDetail iface : session.interfaces().listInterfaces().get())
        {
            if (this.tag.equals(iface.getTag()))
                return iface.getIndex();
        }
        return null;
    }

    protected InterfaceIndex createVXLANTunnel(VPPSimple session) throws InterruptedException, ExecutionException
    {
        System.out.println("Creating VXLAN Tunnel " + this.vni + " to " + this.destinationAddress);
        InterfaceIndex iface = session.vxlan().createVXLANTunnel(this.vni, this.sourceAddress, this.destinationAddress).get();
        session.interfaces().setInterfaceTag(iface, this.tag);
        session.interfaces().setInterfaceUp(iface);
        return iface;
    }

    @Override
    public void apply(VPPSimple session, VPPRecipeContext context) throws InterruptedException, ExecutionException
    {
        this.currentInterfaceIndex = this.findExistingVXLANTunnel(session);
        if (this.currentInterfaceIndex == null) this.currentInterfaceIndex = this.createVXLANTunnel(session);
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
}
