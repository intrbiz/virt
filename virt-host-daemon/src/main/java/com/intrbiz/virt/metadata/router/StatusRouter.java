package com.intrbiz.virt.metadata.router;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Before;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.metadata.Var;
import com.intrbiz.virt.cluster.model.health.MachineHealth;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.metadata.MetadataApp;
import com.intrbiz.virt.model.Machine;

@Prefix("/status/")
public class StatusRouter extends Router<MetadataApp>
{
    private static final Logger logger = Logger.getLogger(StatusRouter.class);
    
    @Before
    @Any("/**")
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
        logger.info("Got status request " + request().getPathInfo() + " for machine: " + request().getRemoteAddress() + " ==> " + cfgMac + ", " + machine.getId());
    }
    
    @Any("/boot")
    @Text
    public String cloudInitFinal(@Var("machine") Machine machine)
    {
        logger.info("Got cloud init final phone home for machine: " + machine.getId() + " " + machine.getName());
        return "OK";
    }
    
    @Post("/health")
    @JSON
    public String guestHealth(@Var("machine") Machine machine, @JSON MachineHealth health)
    {
        app().clusterManager().getMachineStateStore().setMachineHealth(machine.getId(), health);
        return "OK";
    }
}