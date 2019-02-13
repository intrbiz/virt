package com.intrbiz.virt.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.configuration.Configuration;

@XmlRootElement(name = "store-manager")
@XmlType(name = "store-manager")
public class StoreManagerCfg extends Configuration
{
    private static final long serialVersionUID = 1L;
    
    private String type;

    public StoreManagerCfg()
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
