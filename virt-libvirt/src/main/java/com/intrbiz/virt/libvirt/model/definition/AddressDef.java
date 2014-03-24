package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "address")
@XmlType(name = "address")
public class AddressDef
{
    private String type;

    private String controller;

    private String domain;

    private String bus;
    
    private String port;

    private String slot;

    private String function;

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlAttribute(name = "controller")
    public String getController()
    {
        return controller;
    }

    public void setController(String controller)
    {
        this.controller = controller;
    }

    @XmlAttribute(name = "domain")
    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
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

    @XmlAttribute(name = "slot")
    public String getSlot()
    {
        return slot;
    }

    public void setSlot(String slot)
    {
        this.slot = slot;
    }

    @XmlAttribute(name = "function")
    public String getFunction()
    {
        return function;
    }

    public void setFunction(String function)
    {
        this.function = function;
    }

}
