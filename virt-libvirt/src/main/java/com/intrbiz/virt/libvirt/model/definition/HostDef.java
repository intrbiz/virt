package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "host")
@XmlType(name = "host")
public class HostDef
{
    private String name;
    
    private String port;
    
    public HostDef()
    {
        super();
    }
    
    public HostDef(String name, String port)
    {
        super();
        this.name = name;
        this.port = port;
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

    @XmlAttribute(name = "port")
    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }
}
