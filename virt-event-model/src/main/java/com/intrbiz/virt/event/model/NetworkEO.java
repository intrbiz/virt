package com.intrbiz.virt.event.model;

import java.io.Serializable;
import java.util.UUID;

public class NetworkEO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private UUID id;
    
    private String name;
    
    private int vxlanid;
    
    private String type;
    
    private String purpose;
    
    public NetworkEO()
    {
        super();
    }

    public NetworkEO(UUID id, String name, int vxlanid, String type, String purpose)
    {
        super();
        this.id = id;
        this.name = name;
        this.vxlanid = vxlanid;
        this.type = type;
        this.purpose = purpose;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getVxlanid()
    {
        return vxlanid;
    }

    public void setVxlanid(int vxlanid)
    {
        this.vxlanid = vxlanid;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPurpose()
    {
        return purpose;
    }

    public void setPurpose(String purpose)
    {
        this.purpose = purpose;
    }

    @Override
    public String toString()
    {
        return "NetworkEO[id=" + id + ", name=" + name + ", vxlanid=" + vxlanid + ", type=" + type + ", purpose=" + purpose + "]";
    }
}
