package com.intrbiz.virt.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.configuration.Configuration;
import com.intrbiz.util.pool.database.DatabasePoolConfiguration;

@XmlRootElement(name = "virt-host")
@XmlType(name = "virt-host")
public class VirtHostCfg extends Configuration
{
    private static final long serialVersionUID = 1L;

    private NetManagerCfg netManager;

    private StoreManagerCfg storeManager;

    private VirtManagerCfg virtManager;
    
    private ZoneCfg zone;
    
    private DatabasePoolConfiguration database;
    
    private Set<String> capabilities = new HashSet<String>();
    
    private String placementGroup;

    public VirtHostCfg()
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

    @XmlElementRef(type = NetManagerCfg.class)
    public NetManagerCfg getNetManager()
    {
        return netManager;
    }

    public void setNetManager(NetManagerCfg netManager)
    {
        this.netManager = netManager;
    }

    @XmlElementRef(type = StoreManagerCfg.class)
    public StoreManagerCfg getStoreManager()
    {
        return storeManager;
    }

    public void setStoreManager(StoreManagerCfg storeManager)
    {
        this.storeManager = storeManager;
    }

    @XmlElementRef(type = VirtManagerCfg.class)
    public VirtManagerCfg getVirtManager()
    {
        return virtManager;
    }

    public void setVirtManager(VirtManagerCfg virtManager)
    {
        this.virtManager = virtManager;
    }

    @XmlElementRef(type = ZoneCfg.class)
    public ZoneCfg getZone()
    {
        return zone;
    }

    public void setZone(ZoneCfg zone)
    {
        this.zone = zone;
    }
    
    @XmlElementWrapper(name = "capabilities")
    @XmlElement(name = "capability")
    public Set<String> getCapabilities()
    {
        return capabilities;
    }

    public void setCapabilities(Set<String> capabilities)
    {
        this.capabilities = capabilities;
    }

    @XmlAttribute(name = "placement-group")
    public String getPlacementGroup()
    {
        return placementGroup;
    }

    public void setPlacementGroup(String placementGroup)
    {
        this.placementGroup = placementGroup;
    }

    public static VirtHostCfg read(File file) throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(VirtHostCfg.class);
        Unmarshaller u = ctx.createUnmarshaller();
        return (VirtHostCfg) u.unmarshal(file);
    }

    public static void write(File file, VirtHostCfg cfg) throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(VirtHostCfg.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.marshal(cfg, file);
    }
}
