package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "features")
@XmlType(name = "features")
public class FeaturesDef
{
    private ACPI acpi;

    private APIC apic;

    private PAE pae;

    @XmlElementRef(type = ACPI.class)
    public ACPI getAcpi()
    {
        return acpi;
    }

    public void setAcpi(ACPI acpi)
    {
        this.acpi = acpi;
    }

    @XmlElementRef(type = APIC.class)
    public APIC getApic()
    {
        return apic;
    }

    public void setApic(APIC apic)
    {
        this.apic = apic;
    }

    @XmlElementRef(type = PAE.class)
    public PAE getPae()
    {
        return pae;
    }

    public void setPae(PAE pae)
    {
        this.pae = pae;
    }

    @XmlRootElement(name = "acpi")
    @XmlType(name = "acpi")
    public static class ACPI
    {
    }

    @XmlRootElement(name = "apic")
    @XmlType(name = "apic")
    public static class APIC
    {
    }

    @XmlRootElement(name = "pae")
    @XmlType(name = "pae")
    public static class PAE
    {
    }
}
