package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "channel")
@XmlType(name = "channel")
public class ChannelDef
{
    private String type;
    
    private SourceDef source;
    
    private ProtocolDef protocol;

    private TargetDef target;
    
    private AddressDef address;

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlElementRef(type = TargetDef.class)
    public TargetDef getTarget()
    {
        return target;
    }

    public void setTarget(TargetDef target)
    {
        this.target = target;
    }

    @XmlElementRef(type = SourceDef.class)
    public SourceDef getSource()
    {
        return source;
    }

    public void setSource(SourceDef source)
    {
        this.source = source;
    }

    @XmlElementRef(type = ProtocolDef.class)
    public ProtocolDef getProtocol()
    {
        return protocol;
    }

    public void setProtocol(ProtocolDef protocol)
    {
        this.protocol = protocol;
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
