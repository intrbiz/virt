package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "source")
@XmlType(name = "source")
public class SourceDef
{
    private String file;

    private String bridge;
    
    public SourceDef()
    {
        super();
    }
    
    public SourceDef(String file, String bridge)
    {
        super();
        this.file = file;
        this.bridge = bridge;
    }
    
    public static final SourceDef bridge(String bridge)
    {
        return new SourceDef(null, bridge);
    }
    
    public static final SourceDef file(String file)
    {
        return new SourceDef(file, null);
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

}
