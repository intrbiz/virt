package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "cpu")
@XmlType(name = "cpu")
public class CPUDef
{
    private String mode;
    
    private String match;
    
    private String check;
    
    private ModelDef model;
    
    @XmlAttribute(name = "mode")
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XmlAttribute(name = "match")
    public String getMatch()
    {
        return match;
    }

    public void setMatch(String match)
    {
        this.match = match;
    }

    @XmlAttribute(name = "check")
    public String getCheck()
    {
        return check;
    }

    public void setCheck(String check)
    {
        this.check = check;
    }

    @XmlElementRef(type = ModelDef.class)
    public ModelDef getModel()
    {
        return model;
    }

    public void setModel(ModelDef model)
    {
        this.model = model;
    }
}
