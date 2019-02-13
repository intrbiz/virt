package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "metadata")
@XmlType(name = "metadata")
public class MetadataDef
{
    private IntrbizMetadataDef virt;

    public MetadataDef()
    {
        super();
    }

    public MetadataDef(IntrbizMetadataDef virt)
    {
        super();
        this.virt = virt;
    }

    @XmlElementRef(type = IntrbizMetadataDef.class)
    public IntrbizMetadataDef getVirt()
    {
        return virt;
    }

    public void setVirt(IntrbizMetadataDef virt)
    {
        this.virt = virt;
    }
}
