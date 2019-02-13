package com.intrbiz.virt.metadata.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineMetadataNoCloud
{
    @JsonProperty("instance-id")
    private UUID id;
    
    @JsonProperty("hostname")
    private String hostname;
    
    @JsonProperty("public-keys")
    private List<String> sshKeys = new LinkedList<String>();
    
    public MachineMetadataNoCloud()
    {
        super();
    }

    public static MachineMetadataNoCloud metadata()
    {
        return new MachineMetadataNoCloud();
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

    public List<String> getSshKeys()
    {
        return sshKeys;
    }

    public void setSshKeys(List<String> sshKeys)
    {
        this.sshKeys = sshKeys;
    }
    
    public MachineMetadataNoCloud withMachine(UUID id, String hostname)
    {
        this.id = id;
        this.hostname = hostname;
        return this;
    }
    
    public MachineMetadataNoCloud withSSHKey(String... sshKeys)
    {
        for (String sshKey : sshKeys)
        {
            this.sshKeys.add(sshKey);
        }
        return this;
    }
    
    public MachineMetadataNoCloud withSSHKey(List<String> sshKeys)
    {
        for (String sshKey : sshKeys)
        {
            this.sshKeys.add(sshKey);
        }
        return this;
    }
}
