package com.intrbiz.virt.event.model;

import java.io.Serializable;
import java.util.UUID;

public class AccountEO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private UUID id;
    
    private String name;
    
    public AccountEO()
    {
        super();
    }

    public AccountEO(UUID id, String name)
    {
        super();
        this.id = id;
        this.name = name;
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

    @Override
    public String toString()
    {
        return "AccountEO[id=" + id + ", name=" + name + "]";
    }
}
