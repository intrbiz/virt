package com.intrbiz.virt.dash.router;

import java.io.IOException;
import java.util.UUID;

import com.intrbiz.Util;
import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidAccessTokenForURL;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.dash.cfg.VirtGuestImage;
import com.intrbiz.virt.dash.image.VirtGuestImager;
import com.intrbiz.virt.dash.model.VirtGuest;
import com.intrbiz.virt.dash.model.VirtHost;
import com.intrbiz.virt.dash.model.VirtGuest.GuestState;
import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.GraphicsDef;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;

// Routes for the URL root
@Prefix("/")
@Template("layout/main")
@RequireValidPrincipal()
public class AppRouter extends Router<App>
{
    @Any("/")
    public void index()
    {
        model("hosts", ((App) this.app()).getRunningHosts());
        encode("index");
    }

    @Any("/host/:host")
    public void host(String host)
    {
        model("host", ((App) this.app()).getHost(host));
        encode("host");
    }

    @Any("/guest/:host/:guest")
    public void guest(String host, String guest)
    {
        VirtHost virtHost = model("host", ((App) this.app()).getHost(host));
        model("guest", virtHost.getGuest(guest));
        encode("guest");
    }

    @Get("/start/guest/:host/:guest")
    public void startGuest(String host, String guest) throws IOException
    {
        VirtHost virtHost = model("host", ((App) this.app()).getHost(host));
        VirtGuest virtGuest = model("guest", virtHost.getGuest(guest));
        //
        try (LibVirtAdapter lv = LibVirtAdapter.connect(virtHost.getUrl()))
        {
            LibVirtDomain dom = lv.lookupDomainByName(guest);
            if (dom != null)
            {
                virtGuest.setState(GuestState.STARTING);
                dom.start();
                // TODO
                for (GraphicsDef gfx : dom.getDomainDef().getDevices().getGraphics())
                {
                    if ("vnc".equals(gfx.getType()))
                    {
                        virtGuest.setVncPort(gfx.getPort());
                    }
                }
                this.app().writeWebsockifyConfig();
            }
        }
        //
        String to = header("Referer");
        System.out.println("Referer: " + to);
        redirect(Util.isEmpty(to) ? "/" : to);
    }
    
    @Get("/shutdown/guest/:host/:guest")
    public void shutdownGuest(String host, String guest) throws IOException
    {
        VirtHost virtHost = model("host", ((App) this.app()).getHost(host));
        VirtGuest virtGuest = model("guest", virtHost.getGuest(guest));
        //
        try (LibVirtAdapter lv = LibVirtAdapter.connect(virtHost.getUrl()))
        {
            LibVirtDomain dom = lv.lookupDomainByName(guest);
            if (dom != null)
            {
                virtGuest.setState(GuestState.STOPPING);
                dom.powerOff();
                // TODO
                virtGuest.setVncPort(-1);
                this.app().writeWebsockifyConfig();
            }
        }
        //
        String to = header("Referer");
        System.out.println("Referer: " + to);
        redirect(Util.isEmpty(to) ? "/" : to);
    }
    
    @Get("/terminate/guest/:host/:guest")
    public void terminateGuest(String host, String guest) throws IOException
    {
        VirtHost virtHost = model("host", ((App) this.app()).getHost(host));
        VirtGuest virtGuest = model("guest", virtHost.getGuest(guest));
        //
        try (LibVirtAdapter lv = LibVirtAdapter.connect(virtHost.getUrl()))
        {
            LibVirtDomain dom = lv.lookupDomainByName(guest);
            if (dom != null)
            {
                virtGuest.setState(GuestState.STOPPING);
                dom.terminate();
                // TODO
                virtGuest.setVncPort(-1);
                this.app().writeWebsockifyConfig();
            }
        }
        //
        String to = header("Referer");
        System.out.println("Referer: " + to);
        redirect(Util.isEmpty(to) ? "/" : to);
    }
    
    @Get("/remove/guest/:host/:guest")
    public void removeGuest(String host, String guest) throws IOException
    {
        VirtHost virtHost = model("host", ((App) this.app()).getHost(host));
        VirtGuest virtGuest = model("guest", virtHost.getGuest(guest));
        //
        try (LibVirtAdapter lv = LibVirtAdapter.connect(virtHost.getUrl()))
        {
            LibVirtDomain dom = lv.lookupDomainByName(guest);
            if (dom != null)
            {
                dom.remove();
                virtHost.removeGuest(virtGuest.getName());
                this.app().writeWebsockifyConfig();
            }
        }
        //
        redirect("/host/" + host);
    }
    
    @Get("/console/guest/:host/:guest")
    public void consoleGuest(String host, String guest) throws IOException
    {
        VirtHost virtHost = model("host", ((App) this.app()).getHost(host));
        model("guest", virtHost.getGuest(guest));
        //
        encodeOnly("console");
    }
    
    @Get("/create/guest/:host")
    public void createGuest(String host)
    {
        model("host", ((App) this.app()).getHost(host));
        encode("create");
    }
    
    @Post("/create/guest/:host")
    @RequireValidAccessTokenForURL()
    public void createGuest(String host, @Param("name") String name, @Param("image") String image, @Param("cpus") String cpus, @Param("memory") String memory, @Param("bridge") String bridge, @Param("pool") String pool) throws IOException
    {
        VirtHost virtHost = model("host", ((App) this.app()).getHost(host));
        if (Util.isEmpty(name))
        {
            encode("create");
            return;
        }
        VirtGuestImage img = virtHost.getImage(image);
        // setup the imager
        VirtGuestImager imager = new VirtGuestImager(virtHost, img);
        imager.setName(name);
        imager.setCpuCount(Integer.parseInt(cpus));
        imager.setMemory(Long.parseLong(memory) * 1024 * 1024);
        imager.setBridge(bridge);
        imager.setStoragePool(UUID.fromString(pool));
        // image
        imager.image();
        // redirect to the host
        redirect(path("/host/" + virtHost.getName()));
    }
}
