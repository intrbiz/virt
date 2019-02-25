package com.intrbiz.virt;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.intrbiz.balsa.BalsaApplication;
import com.intrbiz.balsa.listener.http.BalsaHTTPListener;
import com.intrbiz.configuration.Configurable;
import com.intrbiz.data.DataManager;
import com.intrbiz.util.pool.database.DBCPPool;
import com.intrbiz.util.pool.database.DatabasePool;
import com.intrbiz.virt.cluster.HostClusterManager;
import com.intrbiz.virt.config.VirtHostCfg;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.router.HealthRouter;
import com.intrbiz.virt.router.dns.DNSRouter;
import com.intrbiz.virt.router.guest.GuestFilter;
import com.intrbiz.virt.router.guest.GuestMetadataRouter;
import com.intrbiz.virt.router.guest.GuestStatusRouter;
import com.intrbiz.virt.util.ARPTable;

public class VirtHostApp extends BalsaApplication implements Configurable<VirtHostCfg>
{
    private HostClusterManager clusterManager;
    
    private ARPTable arpTable;
    
    private VirtHostCfg config;
    
    private String metadataServerUrl;
    
    private String metadataGateway;
    
    private List<String> metadataNameservers;
    
    private List<String> metadataSearchDomain;
    
    public VirtHostApp()
    {
        super();
    }
    
    public void configure(VirtHostCfg config)
    {
        this.config = config;
        this.arpTable = new ARPTable(config.getNetManager().getStringParameterValue("metadata.server.interface", "metadata_srv"));
        this.metadataServerUrl = config.getStringParameterValue("metadata.server.url", "http://172.16.0.1:8888");
        this.metadataGateway = config.getStringParameterValue("metadata.gateway", "172.16.0.1");
        this.metadataNameservers = Arrays.asList(config.getStringParameterValue("metadata.nameservers", "172.16.0.1").split(", ?"));
        this.metadataSearchDomain = Arrays.asList(config.getStringParameterValue("metadata.search.domain", "local").split(", ?"));
    }
    
    public VirtHostCfg getConfiguration()
    {
        return this.config;
    }

    @Override
    protected void setupDefaultListeners() throws Exception
    {
        this.listener(new BalsaHTTPListener(8888));
    }
    
    @Override
    protected void setupEngines() throws Exception
    {
        // setup the data manager
        DatabasePool pool = new DBCPPool();
        pool.configure(this.config.getDatabase());
        DataManager.getInstance().registerDefaultServer(pool);
        // setup cluster
        this.clusterManager = new HostClusterManager(this.getEnv());
        this.clusterManager.configure(this.config);
    }

    @Override
    protected void startApplication() throws Exception
    {
        // load the DB
        VirtDB.load();   
        // start the cluster manager
        this.clusterManager.start();
    }

    @Override
    protected void setupRouters() throws Exception
    {
        this.router(new GuestFilter());
        this.router(new GuestMetadataRouter());
        this.router(new GuestStatusRouter());
        this.router(new HealthRouter());
        this.router(new DNSRouter());
    }
    
    public HostClusterManager clusterManager()
    {
        return this.clusterManager;
    }
    
    public ARPTable arpTable()
    {
        return this.arpTable;
    }
    
    public String getMetadataServerUrl()
    {
        return metadataServerUrl;
    }

    public String getMetadataGateway()
    {
        return metadataGateway;
    }

    public List<String> getMetadataNameservers()
    {
        return metadataNameservers;
    }

    public List<String> getMetadataSearchDomain()
    {
        return metadataSearchDomain;
    }

    public static File getConfigFile()
    {
        return new File(System.getProperty("virt-host.config", "/etc/virt-host/virt-host.xml"));
    }

    public static void main(String[] args)
    {
        try
        {
            // load the application configuration
            VirtHostCfg config = VirtHostCfg.read(getConfigFile());
            // create the application
            VirtHostApp app = new VirtHostApp();
            app.configure(config);
            app.start();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(0);
        }
    }
}
