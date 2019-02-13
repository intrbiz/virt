package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "hugepages")
@XmlType(name = "hugepages")
public class HugepagesDef
{
    private PageDef page;

    public HugepagesDef()
    {
        super();
    }

    public HugepagesDef(PageDef page)
    {
        super();
        this.page = page;
    }

    @XmlElementRef(type = PageDef.class)
    public PageDef getPage()
    {
        return page;
    }

    public void setPage(PageDef page)
    {
        this.page = page;
    }
}
