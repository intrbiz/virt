package com.intrbiz.virt.dash.cfg;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.Util;
import com.intrbiz.configuration.Configuration;

@XmlRootElement(name = "hosted-dns")
@XmlType(name = "hosted-dns")
public class HostedDNSCfg extends Configuration
{
    private static final long serialVersionUID = 1L;
    
    private String domain;
    
    private List<String> nameservers = new ArrayList<String>();
    
    private String hostMaster;

    public HostedDNSCfg()
    {
        super();
    }

    @XmlAttribute(name = "domain")
    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    @XmlElement(name = "name-server")
    public List<String> getNameservers()
    {
        return nameservers;
    }

    public void setNameservers(List<String> nameservers)
    {
        this.nameservers = nameservers;
    }

    @XmlAttribute(name = "host-master")
    public String getHostMaster()
    {
        return hostMaster;
    }

    public void setHostMaster(String hostMaster)
    {
        this.hostMaster = hostMaster;
    }
    
    public void applyDefaults()
    {
        if (Util.isEmpty(this.domain)) this.domain = "user.intrbiz.cloud";
        if (Util.isEmpty(this.hostMaster)) this.hostMaster = "dns@intrbiz.cloud";
        if (this.nameservers.isEmpty())
        {
            this.nameservers.add("ns1.intrbiz.cloud");
            this.nameservers.add("ns2.intrbiz.cloud");
            this.nameservers.add("ns3.intrbiz.cloud");
        }
    }
}
