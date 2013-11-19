package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "serial")
@XmlType(name = "serial")
public class SerialDef
{
    private String type;

    private TargetDef target;

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlElementRef(type = TargetDef.class)
    public TargetDef getTarget()
    {
        return target;
    }

    public void setTarget(TargetDef target)
    {
        this.target = target;
    }

}
