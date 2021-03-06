package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "controller")
@XmlType(name = "controller")
public class ControllerDef extends DeviceDef
{
    private String type;

    private String index;

    private AddressDef address;
    
    private String model;

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlAttribute(name = "index")
    public String getIndex()
    {
        return index;
    }

    public void setIndex(String index)
    {
        this.index = index;
    }

    @XmlElementRef(type = AddressDef.class)
    public AddressDef getAddress()
    {
        return address;
    }

    public void setAddress(AddressDef address)
    {
        this.address = address;
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
}
