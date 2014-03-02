package com.intrbiz.virt.libvirt.model.definition;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "disk")
@XmlType(name = "disk")
public class DiskDef
{
    private String type;

    private String device;

    private DriverDef driver;

    private SourceDef source;

    private TargetDef target;

    private Sharable sharable;

    private AddressDef address;
    
    public DiskDef()
    {
        super();
    }
    
    public DiskDef(String type, String device, DriverDef driver, SourceDef source, TargetDef target)
    {
        super();
        this.type = type;
        this.device = device;
        this.driver = driver;
        this.source = source;
        this.target = target;
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

    @XmlAttribute(name = "device")
    public String getDevice()
    {
        return device;
    }

    public void setDevice(String device)
    {
        this.device = device;
    }

    @XmlElementRef(type = DriverDef.class)
    public DriverDef getDriver()
    {
        return driver;
    }

    public void setDriver(DriverDef driver)
    {
        this.driver = driver;
    }

    @XmlElementRef(type = SourceDef.class)
    public SourceDef getSource()
    {
        return source;
    }

    public void setSource(SourceDef source)
    {
        this.source = source;
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

    @XmlElementRef(type = Sharable.class)
    public Sharable getSharable()
    {
        return sharable;
    }

    public void setSharable(Sharable sharable)
    {
        this.sharable = sharable;
    }

    @XmlElementRef(type = AddressDef.class)
    public AddressDef getAddress()
    {
        return address;
    }

    public void setAddress(AddressDef address)
    {
        this.address = address;
    }

    @XmlRootElement(name = "sharable")
    @XmlType(name = "sharable")
    public static class Sharable
    {

    }
    
    public static DiskDef read(String def)
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(DiskDef.class);
            Unmarshaller u = ctx.createUnmarshaller();
            return (DiskDef) u.unmarshal(new StringReader(def));
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String toString()
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(DiskDef.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(this, w);
            return w.toString();
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }
}
