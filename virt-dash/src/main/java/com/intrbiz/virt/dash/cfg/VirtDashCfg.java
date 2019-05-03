package com.intrbiz.virt.dash.cfg;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.configuration.Configuration;
import com.intrbiz.util.pool.database.DatabasePoolConfiguration;

@XmlRootElement(name = "virt-dash")
@XmlType(name = "virt-dash")
public class VirtDashCfg extends Configuration
{
    private static final long serialVersionUID = 1L;
    
    private DatabasePoolConfiguration database;
    
    private HostedDNSCfg hostedDns;

    public VirtDashCfg()
    {
        super();
    }

    @XmlElementRef(type = DatabasePoolConfiguration.class)
    public DatabasePoolConfiguration getDatabase()
    {
        return database;
    }

    public void setDatabase(DatabasePoolConfiguration database)
    {
        this.database = database;
    }

    @XmlElementRef(type = HostedDNSCfg.class)
    public HostedDNSCfg getHostedDns()
    {
        return hostedDns;
    }

    public void setHostedDns(HostedDNSCfg hostedDns)
    {
        this.hostedDns = hostedDns;
    }
    
    public void applyDefaults()
    {
        if (this.hostedDns == null) this.hostedDns = new HostedDNSCfg();
        this.hostedDns.applyDefaults();
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
