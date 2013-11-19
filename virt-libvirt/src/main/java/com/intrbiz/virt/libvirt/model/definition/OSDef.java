package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "os")
@XmlType(name = "os")
public class OSDef
{

    private Type type;

    private Boot boot;

    @XmlElementRef(type = Type.class)
    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    @XmlElementRef(type = Boot.class)
    public Boot getBoot()
    {
        return boot;
    }

    public void setBoot(Boot boot)
    {
        this.boot = boot;
    }

    @XmlRootElement(name = "type")
    @XmlType(name = "type")
    public static class Type
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

    @XmlRootElement(name = "boot")
    @XmlType(name = "boot")
    public static class Boot
    {

        private String device;

        @XmlAttribute(name = "dev")
        public String getDevice()
        {
            return device;
        }

        public void setDevice(String device)
        {
            this.device = device;
        }

    }
}
