package com.intrbiz.virt.libvirt.model.definition.storage;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="format")
@XmlType(name="format")
public class StorageFormatDef
{
    private String type;

    @XmlAttribute(name="type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
