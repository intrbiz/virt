package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "memoryBacking")
@XmlType(name = "memoryBacking")
public class MemoryBackingDef
{
    private HugepagesDef hugepages;

    public MemoryBackingDef()
    {
        super();
    }

    public MemoryBackingDef(HugepagesDef hugepages)
    {
        super();
        this.hugepages = hugepages;
    }

    @XmlElementRef(type = HugepagesDef.class)
    public HugepagesDef getHugepages()
    {
        return hugepages;
    }

    public void setHugepages(HugepagesDef hugepages)
    {
        this.hugepages = hugepages;
    }

}
