package com.intrbiz.virt.libvirt.model.definition;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "source")
@XmlType(name = "source")
public class SourceDef
{
    public static enum MacVTapMode { BRIDGE, PRIVATE, VEPA }
    
    private String file;

    private String bridge;
    
    private String mode;
    
    private String host;
    
    private String service;
    
    private String protocol;
    
    private String name;
    
    private List<HostDef> hostDef = new LinkedList<HostDef>();
    
    private String type;
    
    private String path;
    
    private String dev;
    
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
    
    public static final SourceDef macVTap(String device, MacVTapMode mode)
    {
        SourceDef def = new SourceDef();
        def.setDev(device);
        def.setMode(mode.toString().toLowerCase());
        return def;
    }
    
    public static final SourceDef file(String file)
    {
        SourceDef def = new SourceDef();
        def.setFile(file);
        return def;
    }
    
    public static final SourceDef device(String devicePath)
    {
        SourceDef def = new SourceDef();
        def.setDev(devicePath);
        return def;
    }
    
    public static final SourceDef rbd(String[] hosts, int port, String image)
    {
        SourceDef def = new SourceDef();
        def.setProtocol("rbd");
        def.setName(image);
        for (String host : hosts)
        {
            def.addHostDef(new HostDef(host.trim(), String.valueOf(port)));
        }
        return def;
    }
    
    /**
     * <source type='unix' path='/var/run/vpp/vm-7afbfb35811a.sock' mode='server'/>
     */
    public static final SourceDef unix(String path, boolean server)
    {
        SourceDef def = new SourceDef();
        def.setType("unix");
        def.setPath(path);
        def.setMode(server ? "server" : "client");
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
    public List<HostDef> getHostDef()
    {
        return hostDef;
    }

    public void setHostDef(List<HostDef> hostDef)
    {
        this.hostDef = hostDef;
    }
    
    public void addHostDef(HostDef hostDef)
    {
        this.hostDef.add(hostDef);
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

    @XmlAttribute(name = "path")
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @XmlAttribute(name = "dev")
    public String getDev()
    {
        return dev;
    }

    public void setDev(String dev)
    {
        this.dev = dev;
    }
}
