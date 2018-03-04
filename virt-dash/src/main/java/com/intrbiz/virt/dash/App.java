package com.intrbiz.virt.dash;

import java.io.File;

import com.intrbiz.balsa.BalsaApplication;
import com.intrbiz.configuration.Configurable;
import com.intrbiz.data.DataManager;
import com.intrbiz.util.pool.database.DatabasePool;
import com.intrbiz.virt.cluster.DashClusterManager;
import com.intrbiz.virt.dash.action.MachineActions;
import com.intrbiz.virt.dash.action.NetworkActions;
import com.intrbiz.virt.dash.action.VolumeActions;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.dash.express.MachineStatusClass;
import com.intrbiz.virt.dash.router.KeysRouter;
import com.intrbiz.virt.dash.router.LoginRouter;
import com.intrbiz.virt.dash.router.MachineRouter;
import com.intrbiz.virt.dash.router.NetworkRouter;
import com.intrbiz.virt.dash.router.OverviewRouter;
import com.intrbiz.virt.dash.router.ProfileRouter;
import com.intrbiz.virt.dash.router.RegisterRouter;
import com.intrbiz.virt.dash.router.VolumeRouter;
import com.intrbiz.virt.dash.router.admin.AccountsRouter;
import com.intrbiz.virt.dash.router.admin.HostsRouter;
import com.intrbiz.virt.dash.router.admin.ImagesRouter;
import com.intrbiz.virt.dash.router.admin.MachineTypesRouter;
import com.intrbiz.virt.dash.router.admin.MachinesRouter;
import com.intrbiz.virt.dash.router.admin.ZonesRouter;
import com.intrbiz.virt.dash.security.VirtDashSecurityEngine;
import com.intrbiz.virt.data.VirtDB;

public class App extends BalsaApplication implements Configurable<VirtDashCfg>
{
    private VirtDashCfg config;
    
    private DashClusterManager clusterManager;
    
    public App()
    {
        super();
    }
    
    public void configure(VirtDashCfg config) throws Exception
    {
        this.config = config;
    }
    
    public VirtDashCfg getConfiguration()
    {
        return this.config;
    }

    @Override
    protected void setupEngines() throws Exception
    {
        securityEngine(new VirtDashSecurityEngine());
        this.clusterManager = new DashClusterManager(this.getEnv());
    }

    @Override
    protected void setupFunctions() throws Exception
    {
        this.function("status_class", MachineStatusClass.class);
    }

    @Override
    protected void setupActions() throws Exception
    {
        action(new MachineActions());
        action(new NetworkActions());
        action(new VolumeActions());
    }

    @Override
    protected void setupRouters() throws Exception
    {
        router(new LoginRouter());
        router(new OverviewRouter());
        router(new ProfileRouter());
        router(new NetworkRouter());
        router(new MachineRouter());
        router(new VolumeRouter());
        router(new KeysRouter());
        router(new RegisterRouter());
        // admin
        router(new MachineTypesRouter());
        router(new ImagesRouter());
        router(new AccountsRouter());
        router(new HostsRouter());
        router(new MachinesRouter());
        router(new ZonesRouter());
    }

    @Override
    protected void startApplication() throws Exception
    {
        // setup the data manager
        DataManager.getInstance().registerDefaultServer(DatabasePool.Default.with().postgresql().url("jdbc:postgresql://127.0.0.1:7654/virt").username("virt").password("virt").build());
        // install the db
        VirtDB.install();
        // start the cluster manager
        this.clusterManager.start();
    }

    public VirtDashCfg getConfig()
    {
        return this.config;
    }
    
    public DashClusterManager getClusterManager()
    {
        return this.clusterManager;
    }
    
    public static File getConfigFile()
    {
        return new File(System.getProperty("virt-dash.config", "/etc/virt-dash/virt-dash.xml"));
    }

    public static void main(String[] args) throws Exception
    {
        // load the application configuration
        VirtDashCfg config = VirtDashCfg.read(getConfigFile());
        // create the application
        App app = new App();
        app.configure(config);
        app.start();
    }
}
