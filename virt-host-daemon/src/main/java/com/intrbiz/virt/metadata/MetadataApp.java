package com.intrbiz.virt.metadata;

import com.intrbiz.balsa.BalsaApplication;
import com.intrbiz.balsa.listener.http.BalsaHTTPListener;
import com.intrbiz.data.DataManager;
import com.intrbiz.util.pool.database.DatabasePool;
import com.intrbiz.virt.cluster.HostClusterManager;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.metadata.router.MetadataRouter;
import com.intrbiz.virt.metadata.router.StatusRouter;
import com.intrbiz.virt.metadata.util.ARPTable;

public class MetadataApp extends BalsaApplication
{
    private HostClusterManager clusterManager;
    
    private final ARPTable arpTable = new ARPTable();
    
    public MetadataApp()
    {
        super();
    }

    @Override
    protected void setupDefaultListeners() throws Exception
    {
        this.listener(new BalsaHTTPListener(8888));
    }
    
    @Override
    protected void setupEngines() throws Exception
    {
        this.clusterManager = new HostClusterManager(this.getEnv());
    }

    @Override
    protected void startApplication() throws Exception
    {
        // setup the data manager
        DataManager.getInstance().registerDefaultServer(DatabasePool.Default.with().postgresql().url("jdbc:postgresql://127.0.0.1:7654/virt").username("virt").password("virt").build());
        // load the DB
        VirtDB.load();
        // start the cluster manager
        this.clusterManager.start();
    }

    @Override
    protected void setupRouters() throws Exception
    {
        this.router(new MetadataRouter());
        this.router(new StatusRouter());
    }
    
    public HostClusterManager clusterManager()
    {
        return this.clusterManager;
    }
    
    public ARPTable arpTable()
    {
        return this.arpTable;
    }

    public static void main(String[] args) throws Exception
    {
        new MetadataApp().start();
    }
}
