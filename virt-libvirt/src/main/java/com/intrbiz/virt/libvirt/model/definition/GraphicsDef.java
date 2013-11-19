package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "graphics")
@XmlType(name = "graphics")
public class GraphicsDef
{
    private String type;

    private int port;

    private String autoport;

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlAttribute(name = "port")
    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    @XmlAttribute(name = "autoport")
    public String getAutoport()
    {
        return autoport;
    }

    public void setAutoport(String autoport)
    {
        this.autoport = autoport;
    }

}
