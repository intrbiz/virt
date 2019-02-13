package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "vcpu")
@XmlType(name = "vcpu")
public class VCPUDef
{

    private String placement;

    private int count;

    @XmlAttribute(name = "placement")
    public String getPlacement()
    {
        return placement;
    }

    public void setPlacement(String placement)
    {
        this.placement = placement;
    }

    @XmlValue
    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}
