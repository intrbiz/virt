package com.intrbiz.virt.dash.cfg;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.configuration.Configuration;

@XmlRootElement(name = "virt-dash")
@XmlType(name = "virt-dash")
public class VirtDashCfg extends Configuration
{
    private static final long serialVersionUID = 1L;

    public VirtDashCfg()
    {
        super();
    }

    public static VirtDashCfg read(File file) throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(VirtDashCfg.class);
        Unmarshaller u = ctx.createUnmarshaller();
        return (VirtDashCfg) u.unmarshal(file);
    }

    public static void write(File file, VirtDashCfg cfg) throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(VirtDashCfg.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(cfg, file);
    }
}
