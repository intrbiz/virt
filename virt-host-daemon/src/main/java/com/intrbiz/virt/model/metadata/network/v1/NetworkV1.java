package com.intrbiz.virt.model.metadata.network.v1;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkV1
{
    @JsonProperty("version")
    private int version = 1;
    
    @JsonProperty("config")
    private List<NetworkConfigV1> config = new LinkedList<NetworkConfigV1>();
    
    public NetworkV1()
    {
        super();
    }
    
    public static NetworkV1 network()
    {
        return new NetworkV1();
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public List<NetworkConfigV1> getConfig()
    {
        return config;
    }

    public void setConfig(List<NetworkConfigV1> config)
    {
        this.config = config;
    }
    
    public NetworkV1 with(PhysicalNetworkConfigV1... physicals)
    {
        for (PhysicalNetworkConfigV1 physical : physicals)
        {
            this.config.add(physical);
        }
        return this;
    }
    
    public NetworkV1 with(NameserverNetworkConfigV1 nameserver)
    {
        this.config.add(nameserver);
        return this;
    }
    
    public NetworkV1 with(RouteNetworkConfigV1... routes)
    {
        for (RouteNetworkConfigV1 route : routes)
        {
            this.config.add(route);
        }
        return this;
    }
}
