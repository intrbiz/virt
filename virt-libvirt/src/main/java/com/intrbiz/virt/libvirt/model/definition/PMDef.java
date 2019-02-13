package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "pm")
@XmlType(name = "pm")
public class PMDef
{
    private SuspendToMem suspendToMem;

    private SuspendToDisk suspendToDisk;

    @XmlElementRef(type = SuspendToMem.class)
    public SuspendToMem getSuspendToMem()
    {
        return suspendToMem;
    }

    public void setSuspendToMem(SuspendToMem suspendToMem)
    {
        this.suspendToMem = suspendToMem;
    }

    @XmlElementRef(type = SuspendToDisk.class)
    public SuspendToDisk getSuspendToDisk()
    {
        return suspendToDisk;
    }

    public void setSuspendToDisk(SuspendToDisk suspendToDisk)
    {
        this.suspendToDisk = suspendToDisk;
    }

    @XmlRootElement(name = "suspend-to-mem")
    @XmlType(name = "suspend-to-mem")
    public static class SuspendToMem
    {
        private String enabled;

        @XmlAttribute(name = "enabled")
        public String getEnabled()
        {
            return enabled;
        }

        public void setEnabled(String enabled)
        {
            this.enabled = enabled;
        }
    }

    @XmlRootElement(name = "suspend-to-disk")
    @XmlType(name = "suspend-to-disk")
    public static class SuspendToDisk
    {
        private String enabled;

        @XmlAttribute(name = "enabled")
        public String getEnabled()
        {
            return enabled;
        }

        public void setEnabled(String enabled)
        {
            this.enabled = enabled;
        }
    }
}
