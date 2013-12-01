package com.intrbiz.virt.libvirt.model.definition.storage;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.data.DataException;

@XmlRootElement(name="pool")
@XmlType(name="pool")
public class LibVirtStoragePoolDef
{
    private String type;
    
    private String name;
    
    private String uuid;
    
    private CapacityDef capacity;
    
    private AllocationDef allocation;
    
    private AvailableDef available;
    
    private StorageTargetDef target;
    
    private String originalXML;
    
    public LibVirtStoragePoolDef()
    {
        super();
    }

    @XmlAttribute(name="type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlElement(name="name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElement(name="uuid")
    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    @XmlElementRef(type=CapacityDef.class)
    public CapacityDef getCapacity()
    {
        return capacity;
    }

    public void setCapacity(CapacityDef capacity)
    {
        this.capacity = capacity;
    }

    @XmlElementRef(type=AllocationDef.class)
    public AllocationDef getAllocation()
    {
        return allocation;
    }

    public void setAllocation(AllocationDef allocation)
    {
        this.allocation = allocation;
    }

    @XmlElementRef(type=AvailableDef.class)
    public AvailableDef getAvailable()
    {
        return available;
    }

    public void setAvailable(AvailableDef available)
    {
        this.available = available;
    }

    @XmlElementRef(type=StorageTargetDef.class)
    public StorageTargetDef getTarget()
    {
        return target;
    }

    public void setTarget(StorageTargetDef target)
    {
        this.target = target;
    }
    
    @XmlTransient
    public String getOriginalXML()
    {
        return originalXML;
    }

    public void setOriginalXML(String originalXML)
    {
        this.originalXML = originalXML;
    }
    
    public static LibVirtStoragePoolDef read(String def)
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(LibVirtStoragePoolDef.class);
            Unmarshaller u = ctx.createUnmarshaller();
            LibVirtStoragePoolDef d = (LibVirtStoragePoolDef) u.unmarshal(new StringReader(def));
            d.setOriginalXML(def);
            return d;
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to parse storage pool definition XML", e);
        }
    }

    public String toString()
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(LibVirtStoragePoolDef.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(this, w);
            return w.toString();
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to serialise storage pool definition to XML", e);
        }
    }
    
    public LibVirtStoragePoolDef clone()
    {
        return LibVirtStoragePoolDef.read(this.toString());
    }
}
