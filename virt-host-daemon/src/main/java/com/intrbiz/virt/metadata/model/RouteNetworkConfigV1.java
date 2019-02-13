package com.intrbiz.virt.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("route")
public class RouteNetworkConfigV1 extends NetworkConfigV1
{
    @JsonProperty("destination")
    private String destination;
    
    @JsonProperty("gateway")
    private String gateway;
    
    public RouteNetworkConfigV1()
    {
        super();
    }

    public RouteNetworkConfigV1(String destination, String gateway)
    {
        super();
        this.destination = destination;
        this.gateway = gateway;
    }
    
    public static RouteNetworkConfigV1 route(String destination, String gateway)
    {
        return new RouteNetworkConfigV1(destination, gateway);
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
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
