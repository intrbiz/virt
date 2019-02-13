package com.intrbiz.vpp.recipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.model.VNI;
import com.intrbiz.vpp.api.recipe.VPPBridgesRecipe;
import com.intrbiz.vpp.api.recipe.VPPInterfacesRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

/**
 * Build a mesh of VxLAN tunnels between the given host and the given remote hosts for all the given set of VMNetworkIds and destinations
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("vxlan.mesh")
public class VXLANMesh extends VPPRecipe implements VPPInterfacesRecipe, VPPBridgesRecipe
{
    @JsonProperty("source")
    private IPv4Address sourceAddress;

    @JsonProperty("destinations")
    private Set<IPv4Address> destinationAddresses;

    @JsonProperty("networks")
    private Set<VMNetworkId> networks;

    @JsonProperty("mac_age")
    private int macAgeSeconds;

    @JsonProperty("vxlan_split_horizon_group")
    private SplitHorizonGroup vxlanSplitHorizonGroup;
    
    private transient Map<Tag, VXLANTunnel> tunnels = new LinkedHashMap<Tag, VXLANTunnel>();

    private transient Map<BridgeDomainId, Bridge> bridges = new LinkedHashMap<BridgeDomainId, Bridge>();
    
    private transient boolean builtFullMesh = false;

    public VXLANMesh(IPv4Address sourceAddress, Set<IPv4Address> destinationAddresses, Set<VMNetworkId> networks, int macAgeSeconds, SplitHorizonGroup vxlanSplitHorizonGroup)
    {
        this.sourceAddress = Objects.requireNonNull(sourceAddress);
        this.destinationAddresses = Objects.requireNonNull(destinationAddresses);
        this.networks = Objects.requireNonNull(networks);
        this.macAgeSeconds = macAgeSeconds;
        this.vxlanSplitHorizonGroup = Objects.requireNonNull(vxlanSplitHorizonGroup);
        this.buildFullMesh();
    }
    
    public VXLANMesh(IPv4Address sourceAddress)
    {
        this(sourceAddress, new HashSet<IPv4Address>(), new HashSet<VMNetworkId>(), 3600, SplitHorizonGroup.ONE);
    }
    
    public VXLANMesh() {
        super();
    }

    public IPv4Address getSourceAddress()
    {
        return this.sourceAddress;
    }

    public Set<IPv4Address> getDestinationAddresses()
    {
        return Collections.unmodifiableSet(this.destinationAddresses);
    }

    public Set<VMNetworkId> getNetworks()
    {
        return Collections.unmodifiableSet(this.networks);
    }

    public void setSourceAddress(IPv4Address sourceAddress)
    {
        this.sourceAddress = sourceAddress;
    }

    public void setDestinationAddresses(Set<IPv4Address> destinationAddresses)
    {
        this.destinationAddresses = destinationAddresses;
    }

    public void setNetworks(Set<VMNetworkId> networks)
    {
        this.networks = new HashSet<VMNetworkId>(networks);
    }

    public void setMacAgeSeconds(int macAgeSeconds)
    {
        this.macAgeSeconds = macAgeSeconds;
    }

    public void setVxlanSplitHorizonGroup(SplitHorizonGroup vxlanSplitHorizonGroup)
    {
        this.vxlanSplitHorizonGroup = vxlanSplitHorizonGroup;
    }

    public int getMacAgeSeconds()
    {
        return this.macAgeSeconds;
    }

    public SplitHorizonGroup getVxlanSplitHorizonGroup()
    {
        return this.vxlanSplitHorizonGroup;
    }

    @Override
    @JsonIgnore()
    public Collection<VXLANTunnel> getInterfaces()
    {
        this.buildFullMesh();
        return Collections.unmodifiableCollection(this.tunnels.values());
    }

    @JsonIgnore()
    public Collection<VXLANTunnel> getVXLANTunnels()
    {
        this.buildFullMesh();
        return Collections.unmodifiableCollection(this.tunnels.values());
    }

    public Set<VXLANTunnel> getTunnels(VMNetworkId network)
    {
        this.buildFullMesh();
        VNI vni = network.toVNI();
        Set<VXLANTunnel> tunnels = new HashSet<>();
        for (VXLANTunnel tunnel : this.tunnels.values()) {
            if (tunnel.getVni().equals(vni))
                tunnels.add(tunnel);
        }
        return tunnels;
    }

    @Override
    @JsonIgnore()
    public Collection<Bridge> getBridges()
    {
        this.buildFullMesh();
        return Collections.unmodifiableCollection(this.bridges.values());
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore()
    public Bridge getBridge(BridgeDomainId id)
    {
        this.buildFullMesh();
        return this.bridges.get(id);
    }

    public Bridge getBridgeForTunnel(VMNetworkId network)
    {
        this.buildFullMesh();
        return this.bridges.get(network.toBridgeDomain());
    }
    
    public void addDestination(IPv4Address destinationAddress)
    {
        this.addDestinations(new HashSet<IPv4Address>(Arrays.asList(destinationAddress)));
    }
    
    public void addDestinations(Set<IPv4Address> destinationAddresses)
    {
        this.buildFullMesh();
        this.destinationAddresses.addAll(destinationAddresses);
        for (VXLANTunnel tunnel : buildMesh(this.sourceAddress, destinationAddresses, this.networks))
        {
            this.tunnels.put(tunnel.getTag(), tunnel);
        }
    }
    
    public void addNetwork(VMNetworkId network)
    {
        this.addNetworks(new HashSet<VMNetworkId>(Arrays.asList(network)));
    }
    
    public void addNetworks(Set<VMNetworkId> newNetworks)
    {
        this.buildFullMesh();
        this.networks.addAll(newNetworks);
        for (Bridge bridge : buildBridges(newNetworks, this.macAgeSeconds))
        {
            this.bridges.put(bridge.getId(), bridge);
        }
        for (VXLANTunnel tunnel : buildMesh(this.sourceAddress, this.destinationAddresses, newNetworks))
        {
            this.tunnels.put(tunnel.getTag(), tunnel);
        }
    }

    protected void createTunnels(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (VXLANTunnel tunnel : this.tunnels.values())
        {
            tunnel.apply(session);
        }
    }

    protected void createdBridges(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (Bridge bridge : this.bridges.values())
        {
            bridge.apply(session);
        }
    }

    protected void addTunnelsToBridges(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (VXLANTunnel tunnel : this.tunnels.values())
        {
            Bridge bridge = this.getBridgeForTunnel(VMNetworkId.fromVNI(tunnel.getVni()));
            System.out.println("Adding tunnel " + tunnel.getCurrentInterfaceIndex() + " to bridge " + bridge.getId());
            session.bridge().addInterfaceToBridgeDomain(bridge.getId(), tunnel.getCurrentInterfaceIndex(), this.vxlanSplitHorizonGroup);
        }
    }
    
    protected void buildFullMesh()
    {
        if (! this.builtFullMesh) {
            for (VXLANTunnel tunnel : buildMesh(this.sourceAddress, this.destinationAddresses, this.networks))
            {
                this.tunnels.putIfAbsent(tunnel.getTag(), tunnel);
            }
            for (Bridge bridge : buildBridges(this.networks, this.macAgeSeconds))
            {
                this.bridges.putIfAbsent(bridge.getId(), bridge);
            }
            this.builtFullMesh = true;
        }
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        this.buildFullMesh();
        // create the bridges
        this.createdBridges(session);
        // create the tunnels
        this.createTunnels(session);
        // add the tunnels to the bridges
        this.addTunnelsToBridges(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }

    public static List<VXLANTunnel> buildMesh(IPv4Address sourceAddress, Set<IPv4Address> destinationAddresses, Set<VMNetworkId> networks)
    {
        Objects.requireNonNull(sourceAddress);
        Objects.requireNonNull(destinationAddresses);
        Objects.requireNonNull(networks);
        List<VXLANTunnel> tunnels = new LinkedList<VXLANTunnel>();
        for (VMNetworkId network : networks)
        {
            for (IPv4Address destinationAddress : destinationAddresses)
            {
                System.out.println("VXLAN Tunnel VNI=" + network.toVNI() + " destination=" + destinationAddress);
                tunnels.add(new VXLANTunnel("vxlan-" + network.toVNI().asHex() + "-" + destinationAddress.asHex(), sourceAddress, destinationAddress, network.toVNI(), new Tag("vni[" + network.asHex() + "]-ip[" + destinationAddress.asHex() + "]")));
            }
        }
        return tunnels;
    }

    public static List<Bridge> buildBridges(Set<VMNetworkId> networks, int macAgeSeconds)
    {
        Objects.requireNonNull(networks);
        List<Bridge> bridges = new LinkedList<Bridge>();
        for (VMNetworkId network : networks)
        {
            System.out.println("Brdige domain id=" + network.toBridgeDomain());
            bridges.add(new Bridge("br" + network.toBridgeDomain().asHex(), network.toBridgeDomain(), macAgeSeconds, new Tag("bridge[" + network.asHex() + "]")));
        }
        return bridges;
    }
}
