package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "param", namespace = "http://intrbiz.net/xml/libvirt")
@XmlType(name = "param", namespace = "http://intrbiz.net/xml/libvirt")
public class IntrbizMetadataParameterDef
{
    private String name;

    private String value;

    public IntrbizMetadataParameterDef()
    {
        super();
    }

    public IntrbizMetadataParameterDef(String name, String value)
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
