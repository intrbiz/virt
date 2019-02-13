package com.intrbiz.virt.metadata.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("physical")
public class PhysicalNetworkConfigV1 extends NetworkConfigV1
{
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("mac_address")
    private String macAddress;
    
    @JsonProperty("subnets")
    private List<NetworkSubnetV1> subnets = new LinkedList<NetworkSubnetV1>();
    
    public PhysicalNetworkConfigV1()
    {
        super();
    }

    public PhysicalNetworkConfigV1(String name, String macAddress)
    {
        super();
        this.name = name;
        this.macAddress = macAddress;
    }
    
    public static PhysicalNetworkConfigV1 physical(String name, String macAddress)
    {
        return new PhysicalNetworkConfigV1(name, macAddress);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }

    public List<NetworkSubnetV1> getSubnets()
    {
        return subnets;
    }

    public void setSubnets(List<NetworkSubnetV1> subnets)
    {
        this.subnets = subnets;
    }
    
    public PhysicalNetworkConfigV1 dhcpAddress()
    {
        this.subnets.add(NetworkSubnetV1.dhcpAddress());
        return this;
    }
    
    public PhysicalNetworkConfigV1 staticAddress(String address)
    {
        this.subnets.add(NetworkSubnetV1.staticAddress(address));
        return this;
    }
    
    public PhysicalNetworkConfigV1 staticAddress(String address, String gateway)
    {
        this.subnets.add(NetworkSubnetV1.staticAddress(address, gateway));
        return this;
    }
}
