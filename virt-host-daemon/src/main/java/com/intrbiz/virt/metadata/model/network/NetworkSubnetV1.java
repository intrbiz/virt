package com.intrbiz.virt.metadata.model.network;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkSubnetV1
{
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("address")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;
    
    @JsonProperty("gateway")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gateway;
    
    public NetworkSubnetV1()
    {
        super();
    }

    public NetworkSubnetV1(String type, String address, String gateway)
    {
        super();
        this.type = type;
        this.address = address;
        this.gateway = gateway;
    }
    
    public static NetworkSubnetV1 dhcpAddress()
    {
        return new NetworkSubnetV1("dhcp", null, null);
    }
    
    public static NetworkSubnetV1 staticAddress(String address, String gateway)
    {
        return new NetworkSubnetV1("static", address, gateway);
    }
    
    public static NetworkSubnetV1 staticAddress(String address)
    {
        return staticAddress(address, null);
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(String gateway)
    {
        this.gateway = gateway;
    }
}
