package com.intrbiz.virt.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.configuration.Configuration;

@XmlRootElement(name = "net-manager")
@XmlType(name = "net-manager")
public class NetManagerCfg extends Configuration
{
    private static final long serialVersionUID = 1L;
    
    private String type;

    public NetManagerCfg()
    {
        super();
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
