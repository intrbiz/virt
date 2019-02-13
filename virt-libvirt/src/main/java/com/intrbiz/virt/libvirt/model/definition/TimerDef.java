package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "timer")
@XmlType(name = "timer")
public class TimerDef
{
    private String name;

    private String tickpolicy;

    private String present;
    
    public TimerDef()
    {
        super();
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

    @XmlAttribute(name = "tickpolicy")
    public String getTickpolicy()
    {
        return tickpolicy;
    }

    public void setTickpolicy(String tickpolicy)
    {
        this.tickpolicy = tickpolicy;
    }

    @XmlAttribute(name = "present")
    public String getPresent()
    {
        return present;
    }

    public void setPresent(String present)
    {
        this.present = present;
    }
}
