package com.intrbiz.virt.libvirt.model.definition;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

@XmlRootElement(name = "domain")
@XmlType(name = "domain")
public class LibVirtDomainDef
{
    private String type;

    private String name;

    private String uuid;
    
    private MetadataDef metadata;

    private MemoryDef memory;
    
    private MemoryBackingDef memoryBacking;

    private CurrentMemoryDef currentMemory;

    private VCPUDef vcpu;
    
    private CPUDef cpu;

    private OSDef os;
    
    private SysInfoDef sysinfo;

    private FeaturesDef features;

    private ClockDef clock;

    private String onPoweroff;

    private String onReboot;

    private String onCrash;

    private DevicesDef devices;

    private String originalXML;
    
    private PMDef pm;

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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

    @XmlElement(name = "uuid")
    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    @XmlElementRef(type = MetadataDef.class)
    public MetadataDef getMetadata()
    {
        return metadata;
    }

    public void setMetadata(MetadataDef metadata)
    {
        this.metadata = metadata;
    }

    @XmlElementRef(type = MemoryBackingDef.class)
    public MemoryBackingDef getMemoryBacking()
    {
        return memoryBacking;
    }

    public void setMemoryBacking(MemoryBackingDef memoryBacking)
    {
        this.memoryBacking = memoryBacking;
    }

    @XmlElementRef(type = MemoryDef.class)
    public MemoryDef getMemory()
    {
        return memory;
    }

    public void setMemory(MemoryDef memory)
    {
        this.memory = memory;
    }

    @XmlElementRef(type = CurrentMemoryDef.class)
    public CurrentMemoryDef getCurrentMemory()
    {
        return currentMemory;
    }

    public void setCurrentMemory(CurrentMemoryDef currentMemory)
    {
        this.currentMemory = currentMemory;
    }

    @XmlElementRef(type = CPUDef.class)
    public CPUDef getCpu()
    {
        return cpu;
    }

    public void setCpu(CPUDef cpu)
    {
        this.cpu = cpu;
    }

    @XmlElementRef(type = VCPUDef.class)
    public VCPUDef getVcpu()
    {
        return vcpu;
    }

    public void setVcpu(VCPUDef vcpu)
    {
        this.vcpu = vcpu;
    }

    @XmlElementRef(type = OSDef.class)
    public OSDef getOs()
    {
        return os;
    }

    public void setOs(OSDef os)
    {
        this.os = os;
    }

    @XmlElementRef(type = FeaturesDef.class)
    public FeaturesDef getFeatures()
    {
        return features;
    }

    public void setFeatures(FeaturesDef features)
    {
        this.features = features;
    }

    @XmlElementRef(type = ClockDef.class)
    public ClockDef getClock()
    {
        return clock;
    }

    public void setClock(ClockDef clock)
    {
        this.clock = clock;
    }

    @XmlElement(name = "on_poweroff")
    public String getOnPoweroff()
    {
        return onPoweroff;
    }

    public void setOnPoweroff(String onPoweroff)
    {
        this.onPoweroff = onPoweroff;
    }

    @XmlElement(name = "on_reboot")
    public String getOnReboot()
    {
        return onReboot;
    }

    public void setOnReboot(String onReboot)
    {
        this.onReboot = onReboot;
    }

    @XmlElement(name = "on_crash")
    public String getOnCrash()
    {
        return onCrash;
    }

    public void setOnCrash(String onCrash)
    {
        this.onCrash = onCrash;
    }

    @XmlElementRef(type = DevicesDef.class)
    public DevicesDef getDevices()
    {
        return devices;
    }

    public void setDevices(DevicesDef devices)
    {
        this.devices = devices;
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
    
    @XmlElementRef(type = PMDef.class)
    public PMDef getPm()
    {
        return pm;
    }

    public void setPm(PMDef pm)
    {
        this.pm = pm;
    }

    public SysInfoDef getSysinfo()
    {
        return sysinfo;
    }

    public void setSysinfo(SysInfoDef sysinfo)
    {
        this.sysinfo = sysinfo;
    }

    public static LibVirtDomainDef read(File def)
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(LibVirtDomainDef.class);
            Unmarshaller u = ctx.createUnmarshaller();
            LibVirtDomainDef d = (LibVirtDomainDef) u.unmarshal(new FileReader(def));
            return d;
        }
        catch (IOException | JAXBException e)
        {
            throw new DataException("Failed to parse domain definition XML", e);
        } 
    }

    public static LibVirtDomainDef read(String def)
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(LibVirtDomainDef.class);
            Unmarshaller u = ctx.createUnmarshaller();
            LibVirtDomainDef d = (LibVirtDomainDef) u.unmarshal(new StringReader(def));
            d.setOriginalXML(def);
            return d;
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to parse domain definition XML", e);
        }
    }

    public String toString()
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(LibVirtDomainDef.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(this, w);
            return w.toString();
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to serialise domain definition to XML", e);
        }
    }
    
    public LibVirtDomainDef clone()
    {
        return LibVirtDomainDef.read(this.toString());
    }
}
