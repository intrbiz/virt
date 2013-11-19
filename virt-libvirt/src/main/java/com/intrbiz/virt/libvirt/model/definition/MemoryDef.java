package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "memory")
@XmlType(name = "memory")
public class MemoryDef
{

    private String unit;

    private long value;

    @XmlAttribute(name = "unit")
    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    @XmlValue
    public long getValue()
    {
        return value;
    }

    public void setValue(long value)
    {
        this.value = value;
    }
    
    public long getBytesValue()
    {
        if ("B".equalsIgnoreCase(this.getUnit()) || "Bytes".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue();
        }
        else if ("KiB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1024;
        }
        else if ("MiB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1024 * 1024;
        }
        else if ("GiB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1024 * 1024 * 1024;
        }
        else if ("TiB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1024 * 1024 * 1024 * 1024;
        }
        else if ("KB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1000;
        }
        else if ("MB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1000 * 1000;
        }
        else if ("GB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1000 * 1000 * 1000;
        }
        else if ("TB".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1000 * 1000 * 1000 * 1000;
        }
        throw new RuntimeException("Unknown unit");
    }
}
