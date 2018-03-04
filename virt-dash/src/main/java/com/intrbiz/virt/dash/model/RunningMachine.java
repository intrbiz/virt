package com.intrbiz.virt.dash.model;

import java.util.List;
import java.util.UUID;

import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.cluster.model.MachineStatus;
import com.intrbiz.virt.cluster.model.health.MachineHealth;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Image;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.MachineType;
import com.intrbiz.virt.model.MachineVolume;
import com.intrbiz.virt.model.SSHKey;
import com.intrbiz.virt.model.Zone;

public class RunningMachine
{
    private final Machine machine;
    
    private final MachineState state;
    
    private final MachineHealth health;

    public RunningMachine(Machine machine, MachineState state, MachineHealth health)
    {
        super();
        this.machine = machine;
        this.state = state;
        this.health = health;
    }

    public UUID getId()
    {
        return machine.getId();
    }

    public UUID getAccountId()
    {
        return machine.getAccountId();
    }

    public UUID getTypeId()
    {
        return machine.getTypeId();
    }

    public UUID getImageId()
    {
        return machine.getImageId();
    }

    public String getName()
    {
        return machine.getName();
    }

    public String getCfgMac()
    {
        return machine.getCfgMac();
    }

    public UUID getSshKeyId()
    {
        return machine.getSshKeyId();
    }

    public String getDescription()
    {
        return machine.getDescription();
    }

    public String getMetadata()
    {
        return machine.getMetadata();
    }

    public MachineType getType()
    {
        return machine.getType();
    }

    public Image getImage()
    {
        return machine.getImage();
    }

    public SSHKey getSSHKey()
    {
        return machine.getSSHKey();
    }

    public MachineStatus getStatus()
    {
        return state == null ? MachineStatus.DEFINED : state.getStatus();
    }

    public String getHost()
    {
        return state == null ? null : state.getHost();
    }
    
    public MachineHealth getHealth()
    {
        return this.health;
    }

    public Zone getZone()
    {
        return machine.getZone();
    }

    public List<MachineNIC> getInterfaces()
    {
        return machine.getInterfaces();
    }

    public List<MachineVolume> getVolumes()
    {
        return machine.getVolumes();
    }

    public Account getAccount()
    {
        return machine.getAccount();
    }
    
    
}
