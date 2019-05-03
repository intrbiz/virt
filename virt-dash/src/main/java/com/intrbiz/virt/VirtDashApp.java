package com.intrbiz.virt;

import java.io.File;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.intrbiz.balsa.BalsaApplication;
import com.intrbiz.configuration.Configurable;
import com.intrbiz.data.DataManager;
import com.intrbiz.util.pool.database.DBCPPool;
import com.intrbiz.util.pool.database.DatabasePool;
import com.intrbiz.virt.cluster.DashClusterManager;
import com.intrbiz.virt.dash.action.CertificateActions;
import com.intrbiz.virt.dash.action.LoadBalancerActions;
import com.intrbiz.virt.dash.action.MachineActions;
import com.intrbiz.virt.dash.action.NetworkActions;
import com.intrbiz.virt.dash.action.VolumeActions;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.dash.express.MachineStatusClass;
import com.intrbiz.virt.dash.router.HealthRouter;
import com.intrbiz.virt.dash.router.LoginRouter;
import com.intrbiz.virt.dash.router.admin.AccountsRouter;
import com.intrbiz.virt.dash.router.admin.HostsRouter;
import com.intrbiz.virt.dash.router.admin.ImagesRouter;
import com.intrbiz.virt.dash.router.admin.LoadBalancerPoolRouter;
import com.intrbiz.virt.dash.router.admin.MachineTypesRouter;
import com.intrbiz.virt.dash.router.admin.MachinesRouter;
import com.intrbiz.virt.dash.router.admin.NetworksRouter;
import com.intrbiz.virt.dash.router.admin.ZonesRouter;
import com.intrbiz.virt.dash.router.api.APIRouter;
import com.intrbiz.virt.dash.router.api.AccountAPIRouter;
import com.intrbiz.virt.dash.router.api.DNSAPIRouter;
import com.intrbiz.virt.dash.router.api.MachineAPIRouter;
import com.intrbiz.virt.dash.router.api.NetworkPIRouter;
import com.intrbiz.virt.dash.router.api.SSHKeysAPIRouter;
import com.intrbiz.virt.dash.router.api.VolumeAPIRouter;
import com.intrbiz.virt.dash.router.dashboard.CertificateRouter;
import com.intrbiz.virt.dash.router.dashboard.DNSRouter;
import com.intrbiz.virt.dash.router.dashboard.KeysRouter;
import com.intrbiz.virt.dash.router.dashboard.LoadBalancerRouter;
import com.intrbiz.virt.dash.router.dashboard.MachineRouter;
import com.intrbiz.virt.dash.router.dashboard.NetworkRouter;
import com.intrbiz.virt.dash.router.dashboard.OverviewRouter;
import com.intrbiz.virt.dash.router.dashboard.ProfileRouter;
import com.intrbiz.virt.dash.router.dashboard.VolumeRouter;
import com.intrbiz.virt.dash.router.internal.ACMEWellKnownRouter;
import com.intrbiz.virt.dash.router.internal.DNSLookupRouter;
import com.intrbiz.virt.dash.router.internal.LoadBalancerConfigRouter;
import com.intrbiz.virt.dash.router.register.RegisterRouter;
import com.intrbiz.virt.dash.security.VirtDashSecurityEngine;
import com.intrbiz.virt.data.VirtDB;

public class VirtDashApp extends BalsaApplication implements Configurable<VirtDashCfg>
{
    private VirtDashCfg config;
    
    private DashClusterManager clusterManager;
    
    public VirtDashApp()
    {
        super();
        this.config = new VirtDashCfg();
        this.config.applyDefaults();
    }
    
    public void configure(VirtDashCfg config) throws Exception
    {
        this.config = config;
    }
    
    public VirtDashCfg getConfiguration()
    {
        return this.config;
    }
    
    public String getLetsEncryptUrl()
    {
        return this.config.getStringParameterValue("acme.url", "acme://letsencrypt.org/staging");
    }
    
    public String getHostedDomain()
    {
        return this.config.getHostedDns().getDomain();
    }

    @Override
    protected void setupEngines() throws Exception
    {
        // Our security manager
        securityEngine(new VirtDashSecurityEngine());
        // setup the data manager
        DatabasePool pool = new DBCPPool();
        pool.configure(this.config.getDatabase());
        DataManager.getInstance().registerDefaultServer(pool);
        // setup cluster
        this.clusterManager = new DashClusterManager(this.getEnv());
        this.clusterManager.configure(this.config);
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
        action(new CertificateActions(this.getLetsEncryptUrl(), this.getHostedDomain()));
        action(new LoadBalancerActions());
    }

    @Override
    protected void setupRouters() throws Exception
    {
        // Health
        router(new HealthRouter());
        // Login and Signup
        router(new LoginRouter());
        router(new RegisterRouter());
        // Dashboard
        router(new OverviewRouter());
        router(new ProfileRouter());
        router(new NetworkRouter());
        router(new MachineRouter());
        router(new VolumeRouter());
        router(new KeysRouter());
        router(new DNSRouter());
        router(new CertificateRouter());
        router(new LoadBalancerRouter());
        // API
        router(new APIRouter());
        router(new AccountAPIRouter());
        router(new MachineAPIRouter());
        router(new VolumeAPIRouter());
        router(new NetworkPIRouter());
        router(new SSHKeysAPIRouter());
        router(new DNSAPIRouter());
        // admin
        router(new MachineTypesRouter());
        router(new ImagesRouter());
        router(new AccountsRouter());
        router(new HostsRouter());
        router(new MachinesRouter());
        router(new NetworksRouter());
        router(new ZonesRouter());
        router(new LoadBalancerPoolRouter());
        // internal
        router(new DNSLookupRouter(this.config.getHostedDns()));
        router(new LoadBalancerConfigRouter());
        router(new ACMEWellKnownRouter());
    }

    @Override
    protected void startApplication() throws Exception
    {
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
        return new File(System.getProperty("virt-dash.config", "/etc/virt/dash.xml"));
    }

    public static void main(String[] args) throws Exception
    {
        try
        {
            // Add BouncyCastle security provider for ACME
            Security.addProvider(new BouncyCastleProvider());
            // load the application configuration
            VirtDashCfg config = VirtDashCfg.read(getConfigFile());
            // create the application
            VirtDashApp app = new VirtDashApp();
            app.configure(config);
            app.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
