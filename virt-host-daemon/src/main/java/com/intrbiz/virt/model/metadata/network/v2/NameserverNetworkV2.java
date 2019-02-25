package com.intrbiz.virt.model.metadata.network.v2;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NameserverNetworkV2
{
    @JsonProperty("address")
    private List<String> nameservers = new LinkedList<String>();

    @JsonProperty("search")
    private List<String> searchDomains = new LinkedList<String>();

    public NameserverNetworkV2()
    {
        super();
    }

    public NameserverNetworkV2(List<String> nameservers, List<String> searchDomains)
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

    public static NameserverNetworkV2 nameserver(List<String> nameservers, List<String> searchDomains)
    {
        return new NameserverNetworkV2(nameservers, searchDomains);
    }
    
    public static NameserverNetworkV2 nameserver()
    {
        return new NameserverNetworkV2();
    }

    public NameserverNetworkV2 nameservers(String... nameservers)
    {
        for (String nameserver : nameservers)
        {
            this.nameservers.add(nameserver);
        }
        return this;
    }

    public NameserverNetworkV2 searchDomains(String... searchDomains)
    {
        for (String searchDomain : searchDomains)
        {
            this.searchDomains.add(searchDomain);
        }
        return this;
    }
}
