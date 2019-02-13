package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "rng")
@XmlType(name = "rng")
public class RNGDef
{
    private String model;

    private BackendDef backend;

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

    @XmlElementRef(type = BackendDef.class)
    public BackendDef getBackend()
    {
        return backend;
    }

    public void setBackend(BackendDef backend)
    {
        this.backend = backend;
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
