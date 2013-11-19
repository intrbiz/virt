package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "model")
@XmlType(name = "model")
public class ModelDef
{
    private String type;
    
    private int vram;
    
    private int heads;

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
    public int getVram()
    {
        return vram;
    }

    public void setVram(int vram)
    {
        this.vram = vram;
    }

    @XmlAttribute(name = "heads")
    public int getHeads()
    {
        return heads;
    }

    public void setHeads(int heads)
    {
        this.heads = heads;
    }
    
    
}
