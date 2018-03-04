package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "source")
@XmlType(name = "source")
public class SourceDef
{
    private String file;

    private String bridge;
    
    private String mode;
    
    private String host;
    
    private String service;
    
    private String protocol;
    
    private String name;
    
    private HostDef hostDef;
    
    public SourceDef()
    {
        super();
    }
    
    public static final SourceDef bridge(String bridge)
    {
        SourceDef def = new SourceDef();
        def.setBridge(bridge);
        return def;
    }
    
    public static final SourceDef file(String file)
    {
        SourceDef def = new SourceDef();
        def.setFile(file);
        return def;
    }
    
    public static final SourceDef rbd(String hosts, int port, String image)
    {
        SourceDef def = new SourceDef();
        def.setProtocol("rbd");
        def.setName(image);
        def.setHostDef(new HostDef(hosts, String.valueOf(port)));
        return def;
    }

    @XmlAttribute(name = "file")
    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    @XmlAttribute(name = "bridge")
    public String getBridge()
    {
        return bridge;
    }

    public void setBridge(String bridge)
    {
        this.bridge = bridge;
    }

    @XmlAttribute(name = "mode")
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XmlAttribute(name = "host")
    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    @XmlAttribute(name = "service")
    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    @XmlAttribute(name = "protocol")
    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
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

    @XmlElementRef(type = HostDef.class)
    public HostDef getHostDef()
    {
        return hostDef;
    }

    public void setHostDef(HostDef hostDef)
    {
        this.hostDef = hostDef;
    }
}
