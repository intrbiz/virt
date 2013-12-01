package com.intrbiz.virt.libvirt.model.definition.storage;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.data.DataException;

@XmlRootElement(name = "volume")
@XmlType(name = "volume")
public class LibVirtStorageVolumeDef
{
    private String name;

    private String key;

    private CapacityDef capacity;

    private AllocationDef allocation;

    private StorageTargetDef target;
    
    private String originalXML;

    public LibVirtStorageVolumeDef()
    {
        super();
    }

    @XmlElement(name = "name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElement(name = "key")
    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    @XmlElementRef(type = CapacityDef.class)
    public CapacityDef getCapacity()
    {
        return capacity;
    }

    public void setCapacity(CapacityDef capacity)
    {
        this.capacity = capacity;
    }

    @XmlElementRef(type = AllocationDef.class)
    public AllocationDef getAllocation()
    {
        return allocation;
    }

    public void setAllocation(AllocationDef allocation)
    {
        this.allocation = allocation;
    }

    @XmlElementRef(type = StorageTargetDef.class)
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
    
    public static LibVirtStorageVolumeDef read(String def)
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(LibVirtStorageVolumeDef.class);
            Unmarshaller u = ctx.createUnmarshaller();
            LibVirtStorageVolumeDef d = (LibVirtStorageVolumeDef) u.unmarshal(new StringReader(def));
            d.setOriginalXML(def);
            return d;
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to parse storage vol definition XML", e);
        }
    }

    public String toString()
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(LibVirtStorageVolumeDef.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(this, w);
            return w.toString();
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to serialise storage vol definition to XML", e);
        }
    }
    
    public LibVirtStorageVolumeDef clone()
    {
        return LibVirtStorageVolumeDef.read(this.toString());
    }
}
