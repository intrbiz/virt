package com.intrbiz.virt.model.metadata.network.v2;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EthernetNetworkV2
{
    @JsonProperty("match")
    private MatchNetworkV2 match;
    
    @JsonProperty("dhcp4")
    private boolean dhcp4 = false;
    
    @JsonProperty("addresses")
    private List<String> addresses = new LinkedList<String>();
    
    @JsonProperty("gateway4")
    private String gateway4;
    
    @JsonProperty("gateway6")
    private String gateway6;
    
    @JsonProperty("nameserver")
    private NameserverNetworkV2 nameserver;
    
    public EthernetNetworkV2()
    {
        super();
    }

    public MatchNetworkV2 getMatch()
    {
        return match;
    }

    public void setMatch(MatchNetworkV2 match)
    {
        this.match = match;
    }

    public boolean isDhcp4()
    {
        return dhcp4;
    }

    public void setDhcp4(boolean dhcp4)
    {
        this.dhcp4 = dhcp4;
    }

    public List<String> getAddresses()
    {
        return addresses;
    }

    public void setAddresses(List<String> addresses)
    {
        this.addresses = addresses;
    }

    public String getGateway4()
    {
        return gateway4;
    }

    public void setGateway4(String gateway4)
    {
        this.gateway4 = gateway4;
    }

    public String getGateway6()
    {
        return gateway6;
    }

    public void setGateway6(String gateway6)
    {
        this.gateway6 = gateway6;
    }

    public NameserverNetworkV2 getNameserver()
    {
        return nameserver;
    }

    public void setNameserver(NameserverNetworkV2 nameserver)
    {
        this.nameserver = nameserver;
    }
    
    public EthernetNetworkV2 withMatch(MatchNetworkV2 match)
    {
        this.match = match;
        return this;
    }
    
    public EthernetNetworkV2 withDhcp4(boolean dhcp4)
    {
        this.dhcp4 = dhcp4;
        return this;
    }
    
    public EthernetNetworkV2 withAddresses(String... addresses)
    {
        for (String address : addresses) 
        {
            this.addresses.add(address);
        }
        return this;
    }
    
    public EthernetNetworkV2 withGateway4(String gateway4)
    {
        this.gateway4 = gateway4;
        return this;
    }
    
    public EthernetNetworkV2 withGateway6(String gateway6)
    {
        this.gateway6 = gateway6;
        return this;
    }
    
    public EthernetNetworkV2 withNameserver(NameserverNetworkV2 nameserver)
    {
        this.nameserver = nameserver;
        return this;
    }
    
    public static EthernetNetworkV2 ethernetDhcp(String mac)
    {
        return new EthernetNetworkV2()
                .withMatch(new MatchNetworkV2().withMacAddress(mac))
                .withDhcp4(true);
    }
    
    public static EthernetNetworkV2 ethernetStatic(String mac, String address)
    {
        return new EthernetNetworkV2()
                .withMatch(new MatchNetworkV2().withMacAddress(mac))
                .withAddresses(address);
    }
}
