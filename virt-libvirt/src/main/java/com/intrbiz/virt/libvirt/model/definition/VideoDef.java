package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "video")
@XmlType(name = "video")
public class VideoDef
{
    private ModelDef model;

    private AddressDef address;

    @XmlElementRef(type = ModelDef.class)
    public ModelDef getModel()
    {
        return model;
    }

    public void setModel(ModelDef model)
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
