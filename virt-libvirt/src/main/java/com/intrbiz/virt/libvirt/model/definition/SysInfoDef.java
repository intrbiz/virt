package com.intrbiz.virt.libvirt.model.definition;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "sysinfo")
@XmlType(name = "sysinfo")
public class SysInfoDef
{
    private String type;

    private List<SysInfoEntryDef> bios = new LinkedList<>();
    
    private List<SysInfoEntryDef> system = new LinkedList<>();
    
    private List<SysInfoEntryDef> baseBoard = new LinkedList<>();
    
    private List<SysInfoEntryDef> chassis = new LinkedList<>();
    
    private List<SysInfoEntryDef> oemStrings = new LinkedList<>();
    
    public SysInfoDef()
    {
        super();
    }

    @XmlAttribute(name ="type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlElementWrapper(name = "bios")
    @XmlElementRef(type = SysInfoEntryDef.class)
    public List<SysInfoEntryDef> getBios()
    {
        return bios;
    }

    public void setBios(List<SysInfoEntryDef> bios)
    {
        this.bios = bios;
    }

    @XmlElementWrapper(name = "system")
    @XmlElementRef(type = SysInfoEntryDef.class)
    public List<SysInfoEntryDef> getSystem()
    {
        return system;
    }

    public void setSystem(List<SysInfoEntryDef> system)
    {
        this.system = system;
    }

    @XmlElementWrapper(name = "baseBoard")
    @XmlElementRef(type = SysInfoEntryDef.class)
    public List<SysInfoEntryDef> getBaseBoard()
    {
        return baseBoard;
    }

    public void setBaseBoard(List<SysInfoEntryDef> baseBoard)
    {
        this.baseBoard = baseBoard;
    }

    @XmlElementWrapper(name = "chassis")
    @XmlElementRef(type = SysInfoEntryDef.class)
    public List<SysInfoEntryDef> getChassis()
    {
        return chassis;
    }

    public void setChassis(List<SysInfoEntryDef> chassis)
    {
        this.chassis = chassis;
    }

    @XmlElementWrapper(name = "oemStrings")
    @XmlElementRef(type = SysInfoEntryDef.class)
    public List<SysInfoEntryDef> getOemStrings()
    {
        return oemStrings;
    }

    public void setOemStrings(List<SysInfoEntryDef> oemStrings)
    {
        this.oemStrings = oemStrings;
    }
}