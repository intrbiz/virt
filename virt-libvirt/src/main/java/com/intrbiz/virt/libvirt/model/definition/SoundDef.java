package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "sound")
@XmlType(name = "sound")
public class SoundDef extends DeviceDef
{
    private String model;

    private AddressDef address;

    @XmlAttribute(name = "model")
    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
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

}
