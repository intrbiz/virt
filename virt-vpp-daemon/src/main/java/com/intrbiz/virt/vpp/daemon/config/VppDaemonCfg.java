package com.intrbiz.virt.vpp.daemon.config;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.configuration.Configuration;

@XmlRootElement(name = "vpp-daemon")
@XmlType(name = "vpp-daemon")
public class VppDaemonCfg extends Configuration
{
    private static final long serialVersionUID = 1L;

    private String recipeStoreDirectory = "/etc/virt/vpp-daemon";

    public VppDaemonCfg()
    {
        super();
    }

    @XmlElement(name = "recipe-store")
    public String getRecipeStoreDirectory()
    {
        return recipeStoreDirectory;
    }

    public void setRecipeStoreDirectory(String recipeStoreDirectory)
    {
        this.recipeStoreDirectory = recipeStoreDirectory;
    }

    public static VppDaemonCfg read(File file) throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(VppDaemonCfg.class);
        Unmarshaller u = ctx.createUnmarshaller();
        return (VppDaemonCfg) u.unmarshal(file);
    }

    public static void write(File file, VppDaemonCfg cfg) throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(VppDaemonCfg.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(cfg, file);
    }
}
