package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "type")
@XmlType(name = "type")
public class TypeDef
{

    private String arch;

    private String machine;

    private String value;

    @XmlAttribute(name = "arch")
    public String getArch()
    {
        return arch;
    }

    public void setArch(String arch)
    {
        this.arch = arch;
    }

    @XmlAttribute(name = "machine")
    public String getMachine()
    {
        return machine;
    }

    public void setMachine(String machine)
    {
        this.machine = machine;
    }

    @XmlValue
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}