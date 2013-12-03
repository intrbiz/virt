package com.intrbiz.virt.dash.cfg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "virt-dash")
@XmlType(name = "virt-dash")
public class VirtDashCfg
{
    private String websockifyConfigFile;

    private int pollPeriod;

    private List<VirtDashUser> users = new LinkedList<VirtDashUser>();

    private List<VirtHostCfg> hosts = new LinkedList<VirtHostCfg>();

    public VirtDashCfg()
    {
        super();
    }

    @XmlElement(name = "websockify-config-file")
    public String getWebsockifyConfigFile()
    {
        return websockifyConfigFile;
    }

    public void setWebsockifyConfigFile(String websockifyConfigFile)
    {
        this.websockifyConfigFile = websockifyConfigFile;
    }

    @XmlElementWrapper(name = "users")
    @XmlElementRef(type = VirtDashUser.class)
    public List<VirtDashUser> getUsers()
    {
        return users;
    }

    public void setUsers(List<VirtDashUser> users)
    {
        this.users = users;
    }

    @XmlElementWrapper(name = "hosts")
    @XmlElementRef(type = VirtHostCfg.class)
    public List<VirtHostCfg> getHosts()
    {
        return hosts;
    }

    public void setHosts(List<VirtHostCfg> hosts)
    {
        this.hosts = hosts;
    }

    @XmlElement(name = "poll-period")
    public int getPollPeriod()
    {
        return pollPeriod;
    }

    public void setPollPeriod(int pollPeriod)
    {
        this.pollPeriod = pollPeriod;
    }

    public VirtDashUser getUser(String email)
    {
        for (VirtDashUser user : this.getUsers())
        {
            if (email.equals(user.getEmail())) return user;
        }
        return null;
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

    public static VirtDashCfg defaults()
    {
        VirtDashCfg cfg = new VirtDashCfg();
        // default user
        VirtDashUser admin = new VirtDashUser("admin", "Administrator");
        admin.hashPassword("admin");
        cfg.getUsers().add(admin);
        //
        return cfg;
    }
}
