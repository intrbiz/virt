package com.intrbiz.virt.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.configuration.Configuration;

@XmlRootElement(name = "zone")
@XmlType(name = "zone")
public class ZoneCfg extends Configuration
{
    private static final long serialVersionUID = 1L;

    public ZoneCfg()
    {
        super();
    }
}
