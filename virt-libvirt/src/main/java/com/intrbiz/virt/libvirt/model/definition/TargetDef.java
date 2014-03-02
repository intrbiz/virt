package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "target")
@XmlType(name = "target")
public class TargetDef
{
    private String dev;

    private String bus;

    private String port;

    private String type;
    
    public TargetDef()
    {
        super();
    }
    
    public TargetDef(String bus, String dev)
    {
        super();
        this.bus = bus;
        this.dev = dev;
    }
    
    public TargetDef(String bus, String dev, String type, String port)
    {
        super();
        this.bus = bus;
        this.dev = dev;
        this.type = type;
        this.port = port;
    }

    @XmlAttribute(name = "dev")
    public String getDev()
    {
        return dev;
    }

    public void setDev(String dev)
    {
        this.dev = dev;
    }

    @XmlAttribute(name = "bus")
    public String getBus()
    {
        return bus;
    }

    public void setBus(String bus)
    {
        this.bus = bus;
    }

    @XmlAttribute(name = "port")
    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
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

}
