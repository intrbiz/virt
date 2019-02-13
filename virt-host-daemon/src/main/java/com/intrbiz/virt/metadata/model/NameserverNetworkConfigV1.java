package com.intrbiz.virt.metadata.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("nameserver")
public class NameserverNetworkConfigV1 extends NetworkConfigV1
{
    @JsonProperty("address")
    private List<String> nameservers = new LinkedList<String>();

    @JsonProperty("search")
    private List<String> searchDomains = new LinkedList<String>();

    public NameserverNetworkConfigV1()
    {
        super();
    }

    public NameserverNetworkConfigV1(List<String> nameservers, List<String> searchDomains)
    {
        super();
        this.nameservers = nameservers;
        this.searchDomains = searchDomains;
    }

    public List<String> getNameservers()
    {
        return nameservers;
    }

    public void setNameservers(List<String> nameservers)
    {
        this.nameservers = nameservers;
    }

    public List<String> getSearchDomains()
    {
        return searchDomains;
    }

    public void setSearchDomains(List<String> searchDomains)
    {
        this.searchDomains = searchDomains;
    }

    public static NameserverNetworkConfigV1 nameserver(List<String> nameservers, List<String> searchDomains)
    {
        return new NameserverNetworkConfigV1(nameservers, searchDomains);
    }
    
    public static NameserverNetworkConfigV1 nameserver()
    {
        return new NameserverNetworkConfigV1();
    }

    public NameserverNetworkConfigV1 nameservers(String... nameservers)
    {
        for (String nameserver : nameservers)
        {
            this.nameservers.add(nameserver);
        }
        return this;
    }

    public NameserverNetworkConfigV1 searchDomains(String... searchDomains)
    {
        for (String searchDomain : searchDomains)
        {
            this.searchDomains.add(searchDomain);
        }
        return this;
    }
}
