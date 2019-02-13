package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "backend")
@XmlType(name = "backend")
public class BackendDef
{
    private String model;

    private String value;

    public BackendDef()
    {
        super();
    }

    public BackendDef(String model, String value)
    {
        super();
        this.model = model;
        this.value = value;
    }

    @XmlAttribute(name = "model")
    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
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
