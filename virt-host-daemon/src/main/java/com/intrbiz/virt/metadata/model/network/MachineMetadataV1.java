package com.intrbiz.virt.metadata.model.network;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineMetadataV1
{
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("hostname")
    private String hostname;
    
    @JsonProperty("zone")
    private String zone;
    
    @JsonProperty("vendor_data")
    private String vendorData;
    
    @JsonProperty("user_data")
    private String userData;
    
    @JsonProperty("public_keys")
    private List<String> sshKeys = new LinkedList<String>();
    
    @JsonProperty("network")
    private NetworkV1 network = new NetworkV1();
    
    public MachineMetadataV1()
    {
        super();
    }

    public static MachineMetadataV1 metadata()
    {
        return new MachineMetadataV1();
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public String getZone()
    {
        return zone;
    }

    public void setZone(String zone)
    {
        this.zone = zone;
    }

    public String getVendorData()
    {
        return vendorData;
    }

    public void setVendorData(String vendorData)
    {
        this.vendorData = vendorData;
    }

    public String getUserData()
    {
        return userData;
    }

    public void setUserData(String userData)
    {
        this.userData = userData;
    }

    public List<String> getSshKeys()
    {
        return sshKeys;
    }

    public void setSshKeys(List<String> sshKeys)
    {
        this.sshKeys = sshKeys;
    }

    public NetworkV1 getNetwork()
    {
        return network;
    }

    public void setNetwork(NetworkV1 network)
    {
        this.network = network;
    }
    
    
    public MachineMetadataV1 withZone(String zone)
    {
        this.zone = zone;
        return this;
    }
    
    public MachineMetadataV1 withMachine(UUID id, String hostname)
    {
        this.id = id;
        this.hostname = hostname;
        return this;
    }
    
    public MachineMetadataV1 withVendorData(String vendorData)
    {
        this.vendorData = vendorData;
        return this;
    }
    
    public MachineMetadataV1 withUserData(String userData)
    {
        this.userData = userData;
        return this;
    }
    
    public MachineMetadataV1 withSSHKey(String... sshKeys)
    {
        for (String sshKey : sshKeys)
        {
            this.sshKeys.add(sshKey);
        }
        return this;
    }
    
    public MachineMetadataV1 withNetwork(NetworkV1 network)
    {
        this.network = network;
        return this;
    }
}
