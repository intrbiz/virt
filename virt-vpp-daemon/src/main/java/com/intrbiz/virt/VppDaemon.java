package com.intrbiz.virt;

import java.io.File;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.BalsaApplication;
import com.intrbiz.balsa.listener.http.BalsaHTTPListener;
import com.intrbiz.virt.vpp.daemon.config.VppDaemonCfg;
import com.intrbiz.virt.vpp.daemon.router.BridgesRouter;
import com.intrbiz.virt.vpp.daemon.router.HealthRouter;
import com.intrbiz.virt.vpp.daemon.router.HelpRouter;
import com.intrbiz.virt.vpp.daemon.router.InterfacesRouter;
import com.intrbiz.virt.vpp.daemon.router.RecipeRouter;
import com.intrbiz.virt.vpp.daemon.router.RoutingRouter;
import com.intrbiz.virt.vpp.daemon.router.VXLANRouter;
import com.intrbiz.virt.vpp.daemon.router.VersionRouter;
import com.intrbiz.vpp.api.VPPRecipeManager;
import com.intrbiz.vpp.api.VPPSimple;

public class VppDaemon extends BalsaApplication
{   
    private static final Logger logger = Logger.getLogger(VppDaemon.class);
    
    private VppDaemonCfg config;
    
    private VPPSimple vpp;
    
    private VPPRecipeManager recipeManager;
    
    public VppDaemon()
    {
        super();
    }
    
    public void configure(VppDaemonCfg config)
    {
        this.config = config;
    }
    
    public VppDaemonCfg getConfiguration()
    {
        return this.config;
    }

    @Override
    protected void setupDefaultListeners() throws Exception
    {
        this.listener(new BalsaHTTPListener(8989));
    }
    
    @Override
    protected void setupEngines() throws Exception
    {
    }

    @Override
    protected void startApplication() throws Exception
    {
        // connect to VPP
        try
        {
            File store = new File(this.config.getRecipeStoreDirectory());
            store.mkdirs();
            this.vpp = VPPSimple.connect(VppDaemon.class.getSimpleName());
            this.recipeManager = VPPRecipeManager.wrap(this.vpp, store);
        }
        catch (Exception e)
        {
            logger.error("Failed to connect to VPP", e);
            throw new RuntimeException("Failed to connect to VPP", e);
        }
    }

    @Override
    protected void setupRouters() throws Exception
    {
        this.router(new HealthRouter());
        this.router(new VersionRouter());
        this.router(new HelpRouter());
        this.router(new InterfacesRouter());
        this.router(new BridgesRouter());
        this.router(new VXLANRouter());
        this.router(new RoutingRouter());
        this.router(new RecipeRouter());
    }
    
    public VPPSimple getVpp()
    {
        return this.vpp;
    }
    
    public VPPRecipeManager getRecipeManager()
    {
        return recipeManager;
    }

    public static File getConfigFile()
    {
        return new File(System.getProperty("vpp-daemon.config", "/etc/virt/vpp-daemon.xml"));
    }

    public static void main(String[] args) throws Exception
    {
        try
        {
            // load the application configuration
            VppDaemonCfg config = VppDaemonCfg.read(getConfigFile());
            // create the application
            VppDaemon app = new VppDaemon();
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
