package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "protocol")
@XmlType(name = "protocol")
public class ProtocolDef
{
    private String type;

    public ProtocolDef()
    {
        super();
    }
    
    public ProtocolDef(String type)
    {
        super();
        this.type = type;
    }

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
