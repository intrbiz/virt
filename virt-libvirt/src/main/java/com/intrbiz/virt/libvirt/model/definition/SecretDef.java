package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "secret")
@XmlType(name = "secret")
public class SecretDef
{
    private String type;
    
    private String uuid;

    public SecretDef()
    {
        super();
    }
    
    public SecretDef(String type, String uuid)
    {
        super();
        this.type = type;
        this.uuid = uuid;
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

    @XmlAttribute(name = "uuid")
    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
}
