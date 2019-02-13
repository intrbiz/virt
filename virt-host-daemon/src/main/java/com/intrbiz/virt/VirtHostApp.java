package com.intrbiz.virt;

import java.io.File;

import com.intrbiz.balsa.BalsaApplication;
import com.intrbiz.balsa.listener.http.BalsaHTTPListener;
import com.intrbiz.configuration.Configurable;
import com.intrbiz.data.DataManager;
import com.intrbiz.util.pool.database.DBCPPool;
import com.intrbiz.util.pool.database.DatabasePool;
import com.intrbiz.virt.cluster.HostClusterManager;
import com.intrbiz.virt.config.VirtHostCfg;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.metadata.router.GuestFilter;
import com.intrbiz.virt.metadata.router.GuestMetadataRouter;
import com.intrbiz.virt.metadata.router.GuestStatusRouter;
import com.intrbiz.virt.metadata.router.HealthRouter;
import com.intrbiz.virt.metadata.util.ARPTable;

public class VirtHostApp extends BalsaApplication implements Configurable<VirtHostCfg>
{
    private HostClusterManager clusterManager;
    
    private final ARPTable arpTable = new ARPTable();
    
    private VirtHostCfg config;
    
    public VirtHostApp()
    {
        super();
    }
    
    public void configure(VirtHostCfg config)
    {
        this.config = config;
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
    }
    
    public HostClusterManager clusterManager()
    {
        return this.clusterManager;
    }
    
    public ARPTable arpTable()
    {
        return this.arpTable;
    }
    
    public static File getConfigFile()
    {
        return new File(System.getProperty("virt-host.config", "/etc/virt-host/virt-host.xml"));
    }

    public static void main(String[] args) throws Exception
    {
        // load the application configuration
        VirtHostCfg config = VirtHostCfg.read(getConfigFile());
        // create the application
        VirtHostApp app = new VirtHostApp();
        app.configure(config);
        app.start();
    }
}
