package com.intrbiz.virt.dash;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBException;

import com.intrbiz.balsa.BalsaApplication;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.dash.cfg.VirtHostCfg;
import com.intrbiz.virt.dash.model.VirtGuest;
import com.intrbiz.virt.dash.model.VirtHost;
import com.intrbiz.virt.dash.poller.LibVirtPoller;
import com.intrbiz.virt.dash.router.AppRouter;
import com.intrbiz.virt.dash.router.LoginRouter;
import com.intrbiz.virt.dash.security.VirtDashSecurityEngine;

public class App extends BalsaApplication
{
    private File configFile;
    
    private VirtDashCfg config;
    
    private ConcurrentMap<String, VirtHost> hosts = new ConcurrentHashMap<String, VirtHost>();
    
    private LibVirtPoller poller;
    
    @Override
    protected void setup() throws Exception
    {
        this.configFile = new File(this.getArgument("config", "/etc/virt-dash/virt-dash.xml"));
        if (! this.configFile.exists()) VirtDashCfg.write(this.configFile, VirtDashCfg.defaults());
        // load the application configuation
        this.config = VirtDashCfg.read(this.configFile);
        // load the config
        for (VirtHostCfg hostCfg : this.config.getHosts())
        {
            VirtHost host = new VirtHost(hostCfg.getName(), hostCfg.getAddress(), hostCfg.getUrl());
            host.getImages().addAll(hostCfg.getGuestImages());
            this.addHost(host);
        }
        // start the poller
        this.poller = new LibVirtPoller(this);
        this.poller.setSleepTime(this.config.getPollPeriod() * 1000);
        this.poller.start();
        // security engine
        securityEngine(new VirtDashSecurityEngine());
        // Setup the application routers
        router(new LoginRouter());
        router(new AppRouter());
    }
    
    public VirtDashCfg getConfig()
    {
        return this.config;
    }
    
    public File getConfigFile()
    {
        return this.configFile;
    }

    public List<VirtHost> getHosts()
    {
        List<VirtHost> l = new LinkedList<VirtHost>();
        l.addAll(this.hosts.values());
        Collections.sort(l);
        return l;
    }
    
    public List<VirtHost> getRunningHosts()
    {
        List<VirtHost> l = new LinkedList<VirtHost>();
        for (VirtHost host : this.hosts.values())
        {
            if (host.isUp()) l.add(host);
        }
        Collections.sort(l);
        return l;
    }
    
    public VirtHost getHost(String name)
    {
        return this.hosts.get(name);
    }
    
    public void addHost(VirtHost host)
    {
        this.hosts.put(host.getName(), host);
    }
    
    public LibVirtPoller getPoller()
    {
        return this.poller;
    }
    
    //
    
    public synchronized void writeWebsockifyConfig()
    {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(this.getConfig().getWebsockifyConfigFile())))
        {
            for (VirtHost host : this.getHosts())
            {
                for (VirtGuest guest : host.getGuests())
                {
                    fw.write(host.getName() + "-" + guest.getName() + ": " + host.getAddress() + ":" + guest.getVncPort() + "\r\n");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public synchronized void writeConfig()
    {
        try
        {
            VirtDashCfg.write(this.getConfigFile(), this.getConfig());
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
    }
}
