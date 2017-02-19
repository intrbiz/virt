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
import com.intrbiz.configuration.Configurable;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.dash.cfg.VirtHostCfg;
import com.intrbiz.virt.dash.model.VirtGuest;
import com.intrbiz.virt.dash.model.VirtHost;
import com.intrbiz.virt.dash.router.AppRouter;
import com.intrbiz.virt.dash.router.LoginRouter;
import com.intrbiz.virt.dash.security.VirtDashSecurityEngine;

public class App extends BalsaApplication implements Configurable<VirtDashCfg>
{
    private VirtDashCfg config;

    private ConcurrentMap<String, VirtHost> hosts = new ConcurrentHashMap<String, VirtHost>();
    
    public App()
    {
        super();
    }
    
    public void configure(VirtDashCfg config) throws Exception
    {
        this.config = config;
        // load the config
        for (VirtHostCfg hostCfg : this.config.getHosts())
        {
            VirtHost host = new VirtHost(hostCfg.getName(), hostCfg.getAddress(), hostCfg.getUrl());
            host.getImages().addAll(hostCfg.getGuestImages());
            this.addHost(host);
        }
    }
    
    public VirtDashCfg getConfiguration()
    {
        return this.config;
    }

    @Override
    protected void setupEngines() throws Exception
    {
        // security engine
        securityEngine(new VirtDashSecurityEngine());
    }

    @Override
    protected void setupFunctions() throws Exception
    {
    }

    @Override
    protected void setupActions() throws Exception
    {
    }

    @Override
    protected void setupRouters() throws Exception
    {
        // Setup the application routers
        router(new LoginRouter());
        router(new AppRouter());
    }

    @Override
    protected void startApplication() throws Exception
    {
        // connect the hosts
        for (VirtHost host : this.getHosts())
        {
            host.connect();
        }
    }

    public VirtDashCfg getConfig()
    {
        return this.config;
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
            VirtDashCfg.write(getConfigFile(), this.getConfig());
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
    }
    
    public static File getConfigFile()
    {
        return new File(System.getProperty("virt-dash.config", "/etc/virt-dash/virt-dash.xml"));
    }

    public static void main(String[] args) throws Exception
    {
        // load the configuration
        File configFile = getConfigFile();
        if (! configFile.exists()) VirtDashCfg.write(configFile, VirtDashCfg.defaults());
        // load the application configuation
        VirtDashCfg config = VirtDashCfg.read(configFile);
        // create the application
        App app = new App();
        app.configure(config);
        app.start();
    }
}
