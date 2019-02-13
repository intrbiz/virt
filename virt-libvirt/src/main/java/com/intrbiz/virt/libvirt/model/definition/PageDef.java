package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "page")
@XmlType(name = "page")
public class PageDef
{
    private long size;

    private String unit;

    private String nodeset;

    public PageDef()
    {
        super();
    }

    public PageDef(long size, String unit, String nodeset)
    {
        super();
        this.size = size;
        this.unit = unit;
        this.nodeset = nodeset;
    }

    @XmlAttribute(name = "size")
    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    @XmlAttribute(name = "unit")
    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    @XmlAttribute(name = "nodeset")
    public String getNodeset()
    {
        return nodeset;
    }

    public void setNodeset(String nodeset)
    {
        this.nodeset = nodeset;
    }
}
