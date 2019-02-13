package com.intrbiz.virt.libvirt.model.definition;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "os")
@XmlType(name = "os")
public class OSDef
{

    private TypeDef type;

    private List<BootDef> boot = new LinkedList<BootDef>();
    
    private NVRAMDef nvram;
    
    private LoaderDef loader;
    
    private SMBIOSDef smbios;

    @XmlElementRef(type = TypeDef.class)
    public TypeDef getType()
    {
        return type;
    }

    public void setType(TypeDef type)
    {
        this.type = type;
    }

    @XmlElementRef(type = BootDef.class)
    public List<BootDef> getBoot()
    {
        return boot;
    }

    public void setBoot(List<BootDef> boot)
    {
        this.boot = boot;
    }

    @XmlElementRef(type = NVRAMDef.class)
    public NVRAMDef getNvram()
    {
        return nvram;
    }

    public void setNvram(NVRAMDef nvram)
    {
        this.nvram = nvram;
    }

    @XmlElementRef(type = LoaderDef.class)
    public LoaderDef getLoader()
    {
        return loader;
    }

    public void setLoader(LoaderDef loader)
    {
        this.loader = loader;
    }

    @XmlElementRef(type = SMBIOSDef.class)
    public SMBIOSDef getSmbios()
    {
        return smbios;
    }

    public void setSmbios(SMBIOSDef smbios)
    {
        this.smbios = smbios;
    }
}
