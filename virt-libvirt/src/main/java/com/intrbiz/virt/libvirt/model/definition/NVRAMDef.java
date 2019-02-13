package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "nvram")
@XmlType(name = "nvram")
public class NVRAMDef
{
    private String template;
    
    private String file;

    @XmlAttribute(name = "template")
    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
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
