package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

public class BytesValue
{
    private String unit;

    private long value;
    
    public BytesValue()
    {
        super();
    }
    
    public BytesValue(long value)
    {
        super();
        this.setBytesValue(value);
    }
    
    public BytesValue(long value, String unit)
    {
        super();
        this.setUnit(unit);
        this.setBytesValue(value);
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

    @XmlValue
    public long getValue()
    {
        return value;
    }

    public void setValue(long value)
    {
        this.value = value;
    }
    
    @XmlTransient()
    public long getBytesValue()
    {
        if ("B".equalsIgnoreCase(this.getUnit()) || "Bytes".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue();
        }
        else if ("KiB".equalsIgnoreCase(this.getUnit()) || "K".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1024;
        }
        else if ("MiB".equalsIgnoreCase(this.getUnit()) || "M".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1024 * 1024;
        }
        else if ("GiB".equalsIgnoreCase(this.getUnit()) || "G".equalsIgnoreCase(this.getUnit()))
        {
            return this.getValue() * 1024 * 1024 * 1024;
        }
        else if ("TiB".equalsIgnoreCase(this.getUnit()) || "T".equalsIgnoreCase(this.getUnit()))
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
    
    public void setBytesValue(long bytes)
    {
        // set the default unit
        if (this.getUnit() == null || this.getUnit().length() == 0) this.setUnit("KiB");
        // convert
        if ("B".equalsIgnoreCase(this.getUnit()) || "Bytes".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes);
            return;
        }
        else if ("KiB".equalsIgnoreCase(this.getUnit()) || "K".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / 1024);
            return;
        }
        else if ("MiB".equalsIgnoreCase(this.getUnit()) || "M".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / (1024 * 1024));
            return;
        }
        else if ("GiB".equalsIgnoreCase(this.getUnit()) || "G".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / (1024 * 1024 * 1024));
            return;
        }
        else if ("TiB".equalsIgnoreCase(this.getUnit()) || "T".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / (1024 * 1024 * 1024 * 1024));
            return;
        }
        else if ("KB".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / 1000);
            return;
        }
        else if ("MB".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / (1000 * 1000));
            return;
        }
        else if ("GB".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / (1000 * 1000 * 1000));
            return;
        }
        else if ("TB".equalsIgnoreCase(this.getUnit()))
        {
            this.setValue(bytes / (1000 * 1000 * 1000 * 1000));
            return;
        }
        throw new RuntimeException("Unknown unit");
    }
}
