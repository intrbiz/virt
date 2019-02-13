package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "entry")
@XmlType(name = "entry")
public class SysInfoEntryDef
{
    private String name;
    
    private String value;
    
    public SysInfoEntryDef()
    {
        super();
    }

    public SysInfoEntryDef(String name, String value)
    {
        super();
        this.name = name;
        this.value = value;
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

    @XmlValue()
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}