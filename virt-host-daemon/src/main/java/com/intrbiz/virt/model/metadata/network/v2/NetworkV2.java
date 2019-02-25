package com.intrbiz.virt.model.metadata.network.v2;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkV2
{
    @JsonProperty("version")
    private int version = 2;
    
    @JsonProperty("ethernets")
    private Map<String, EthernetNetworkV2> ethernets = new HashMap<String, EthernetNetworkV2>();
    
    public NetworkV2()
    {
        super();
    }
    
    public static NetworkV2 network()
    {
        return new NetworkV2();
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public Map<String, EthernetNetworkV2> getEthernets()
    {
        return ethernets;
    }

    public void setEthernets(Map<String, EthernetNetworkV2> ethernets)
    {
        this.ethernets = ethernets;
    }
    
    public NetworkV2 withEthernet(String name, EthernetNetworkV2 ethernet)
    {
        this.ethernets.put(name, ethernet);
        return this;
    }
}
