package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "driver")
@XmlType(name = "driver")
public class DriverDef
{
    private String name;

    private String type;

    private String cache;

    private String io;
    
    public DriverDef()
    {
        super();
    }
    
    public DriverDef(String name, String type, String cache, String io)
    {
        super();
        this.name = name;
        this.type = type;
        this.cache = cache;
        this.io = io;
    }
    
    public DriverDef(String name, String type, String cache)
    {
        super();
        this.name = name;
        this.type = type;
        this.cache = cache;
    }

    @XmlAttribute(name = "name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlAttribute(name = "cache")
    public String getCache()
    {
        return cache;
    }

    public void setCache(String cache)
    {
        this.cache = cache;
    }

    @XmlAttribute(name = "io")
    public String getIo()
    {
        return io;
    }

    public void setIo(String io)
    {
        this.io = io;
    }
    
    public static DriverDef qcow2()
    {
        return new DriverDef("qemu", "qcow2", "writethrough");
    }
    
    public static DriverDef raw()
    {
        return new DriverDef("qemu", "raw", "writethrough");
    }
}
