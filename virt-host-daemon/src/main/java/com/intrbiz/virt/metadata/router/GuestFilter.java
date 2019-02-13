package com.intrbiz.virt.metadata.router;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Before;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.virt.VirtHostApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Machine;

@Prefix("/")
public class GuestFilter extends Router<VirtHostApp>
{
    private static final Logger logger = Logger.getLogger(GuestFilter.class);
    
    @Before
    @Any(value="/(?:metadata|status|meta-data|user-data|network-config).*", regex=true)
    @WithDataAdapter(VirtDB.class)
    public void filterMachine(VirtDB db)
    {
        // translate the client to the machine MAC address
        String cfgMac = app().arpTable().getInstanceMAC(request().getRemoteAddress());
        require(cfgMac != null, "Cannot resolve MAC address");
        // lookup the machine
        Machine machine = db.getMachineByCfgMAC(cfgMac);
        require(machine != null, "No such machine");
        var("machine", machine);
        logger.info("Got metadata request " + request().getPathInfo() + " for machine: " + request().getRemoteAddress() + " ==> " + cfgMac + ", " + machine.getId());
    }
}
