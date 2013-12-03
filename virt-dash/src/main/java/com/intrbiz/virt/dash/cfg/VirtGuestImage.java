package com.intrbiz.virt.dash.cfg;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;

@XmlRootElement(name="guest-image")
@XmlType(name="guest-image")
public class VirtGuestImage
{
    private String name;
    
    private LibVirtDomainDef definition;
    
    public VirtGuestImage()
    {
        super();
    }

    @XmlAttribute(name="name")
    public String getName()
    {
        return name;
    }

    public void setName(String imageName)
    {
        this.name = imageName;
    }

    @XmlElementRef(type=LibVirtDomainDef.class)
    public LibVirtDomainDef getDefinition()
    {
        return definition;
    }

    public void setDefinition(LibVirtDomainDef definition)
    {
        this.definition = definition;
    }
}
