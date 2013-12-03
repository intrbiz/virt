package com.intrbiz.virt.dash.cfg;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "host")
@XmlType(name = "host")
public class VirtHostCfg
{
    private String name;

    private String address;

    private String url;
    
    private List<VirtGuestImage> guestImages = new LinkedList<VirtGuestImage>();

    public VirtHostCfg()
    {
        super();
    }

    @XmlAttribute(name = "name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlAttribute(name = "address")
    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    @XmlAttribute(name = "url")
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @XmlElementRef(type=VirtGuestImage.class)
    public List<VirtGuestImage> getGuestImages()
    {
        return guestImages;
    }

    public void setGuestImages(List<VirtGuestImage> guestImages)
    {
        this.guestImages = guestImages;
    }
}
