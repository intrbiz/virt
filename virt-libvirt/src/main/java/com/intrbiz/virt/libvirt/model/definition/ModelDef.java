package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "model")
@XmlType(name = "model")
public class ModelDef
{
    private String type;
    
    private Integer vram;
    
    private Integer heads;

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlAttribute(name = "vram")
    public Integer getVram()
    {
        return vram;
    }

    public void setVram(Integer vram)
    {
        this.vram = vram;
    }

    @XmlAttribute(name = "heads")
    public Integer getHeads()
    {
        return heads;
    }

    public void setHeads(Integer heads)
    {
        this.heads = heads;
    }
    
    
}
