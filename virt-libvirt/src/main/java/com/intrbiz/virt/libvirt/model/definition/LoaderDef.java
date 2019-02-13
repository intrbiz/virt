package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "loader")
@XmlType(name = "loader")
public class LoaderDef
{
    private String readonly;

    private String type;
    
    private String file;

    @XmlAttribute(name = "readonly")
    public String getReadonly()
    {
        return readonly;
    }

    public void setReadonly(String readonly)
    {
        this.readonly = readonly;
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

    @XmlValue()
    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }
}