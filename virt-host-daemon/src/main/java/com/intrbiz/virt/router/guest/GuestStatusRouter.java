package com.intrbiz.virt.router.guest;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.metadata.Var;
import com.intrbiz.virt.VirtHostApp;
import com.intrbiz.virt.cluster.model.health.MachineHealth;
import com.intrbiz.virt.model.Machine;

@Prefix("/")
public class GuestStatusRouter extends Router<VirtHostApp>
{
    private static final Logger logger = Logger.getLogger(GuestStatusRouter.class);
    
    @Any("/status/boot")
    @Text
    public String cloudInitFinal(@Var("machine") Machine machine)
    {
        logger.info("Got cloud init final phone home for machine: " + machine.getId() + " " + machine.getName());
        return "OK";
    }
    
    @Post("/status/health")
    @JSON
    public String guestHealth(@Var("machine") Machine machine, @JSON MachineHealth health)
    {
        app().clusterManager().getMachineStateStore().setMachineHealth(machine.getId(), health);
        return "OK";
    }
}