package com.intrbiz.virt.model.metadata.network.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchNetworkV2
{
    @JsonProperty("macaddress")
    private String macaddress;

    public MatchNetworkV2()
    {
        super();
    }

    public String getMacaddress()
    {
        return macaddress;
    }

    public void setMacaddress(String macaddress)
    {
        this.macaddress = macaddress;
    }

    public MatchNetworkV2 withMacAddress(String macAddress)
    {
        this.macaddress = macAddress;
        return this;
    }
}
