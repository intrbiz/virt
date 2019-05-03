package com.intrbiz.virt.dash.router.dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.CoalesceMode;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaBoolean;
import com.intrbiz.metadata.IsaInt;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.SessionVar;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.ACMECertificate;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.AdminState;
import com.intrbiz.virt.model.LoadBalancer;
import com.intrbiz.virt.model.LoadBalancerBackendServer;
import com.intrbiz.virt.model.LoadBalancerBackendTarget;
import com.intrbiz.virt.model.LoadBalancerPool;
import com.intrbiz.virt.model.LoadBalancerPoolTCPPort;
import com.intrbiz.virt.model.Machine;

@Prefix("/balancer")
@Template("layout/main")
@RequireValidPrincipal()
public class LoadBalancerRouter extends Router<VirtDashApp>
{
    @Get("/")
    @WithDataAdapter(VirtDB.class)
    public void listBalancers(VirtDB db, @SessionVar("currentAccount") Account currentAccount)
    {
        var("balancers", db.getLoadBalancersForAccount(currentAccount.getId()));
        var("hosted_domain", app().getHostedDomain());
        encode("balancer/index");
    }
    
    @Get("/id/:id")
    @WithDataAdapter(VirtDB.class)
    public void showBalancer(VirtDB db, @IsaUUID UUID id)
    {
        LoadBalancer balancer = var("balancer", notNull(db.getLoadBalancer(id)));
        var("hosted_domain", app().getHostedDomain());
        var("machines", db.getPossibleBackendMachinesForLoadBalancer(balancer.getId()));
        encode("balancer/details");
    }
    
    @Post("/id/:id/add/backend/machine")
    @WithDataAdapter(VirtDB.class)
    public void doAddBackendMachine(VirtDB db, @IsaUUID UUID id, @Param("machine") @IsaUUID UUID machineId, @Param("port") @IsaInt(mandatory = true) Integer port) throws IOException
    {
        LoadBalancer balancer = notNull(db.getLoadBalancer(id));
        Machine machine = notNull(db.getMachine(machineId));
        db.setLoadBalancerBackendServer(new LoadBalancerBackendServer(balancer, machine, port));
        redirect("/balancer/id/" + balancer.getId());
    }
    
    @Get("/id/:id/remove/backend/machine/id/:machineId/port/:machinePort")
    @WithDataAdapter(VirtDB.class)
    public void doRemoveBackendMachine(VirtDB db, @IsaUUID UUID id, @IsaUUID UUID machineId, @IsaInt Integer port) throws IOException
    {
        db.removeLoadBalancerBackendServer(id, machineId, port);
        redirect("/balancer/id/" + id);
    }
    
    @Get("/id/:id/set/backend/machine/id/:machineId/port/:machinePort/:state")
    @WithDataAdapter(VirtDB.class)
    public void doSetBackendMachine(VirtDB db, @IsaUUID UUID id, @IsaUUID UUID machineId, @IsaInt Integer port, String state) throws IOException
    {
        LoadBalancerBackendServer backend = notNull(db.getLoadBalancerBackendServer(id, machineId, port));
        backend.setAdminState(AdminState.valueOf(state.toUpperCase()));
        db.setLoadBalancerBackendServer(backend);
        redirect("/balancer/id/" + id);
    }
    
    @Post("/id/:id/add/backend/target")
    @WithDataAdapter(VirtDB.class)
    public void doAddBackendTarget(VirtDB db, @IsaUUID UUID id, @Param("target") String target, @Param("port") @IsaInt(mandatory = true) Integer port) throws IOException
    {
        LoadBalancer balancer = notNull(db.getLoadBalancer(id));
        db.setLoadBalancerBackendTarget(new LoadBalancerBackendTarget(balancer, target, port));
        redirect("/balancer/id/" + balancer.getId());
    }
    
    @Get("/id/:id/remove/backend/target/:target/port/:port")
    @WithDataAdapter(VirtDB.class)
    public void doRemoveBackendTarget(VirtDB db, @IsaUUID UUID id, String target, @IsaInt Integer port) throws IOException
    {
        db.removeLoadBalancerBackendTarget(id, target, port);
        redirect("/balancer/id/" + id);
    }
    
    @Get("/id/:id/set/backend/target/:target/port/:machinePort/:state")
    @WithDataAdapter(VirtDB.class)
    public void doSetBackendTarget(VirtDB db, @IsaUUID UUID id, String target, @IsaInt Integer port, String state) throws IOException
    {
        LoadBalancerBackendTarget backend = notNull(db.getLoadBalancerBackendTarget(id, target, port));
        backend.setAdminState(AdminState.valueOf(state.toUpperCase()));
        db.setLoadBalancerBackendTarget(backend);
        redirect("/balancer/id/" + id);
    }
    
    @Get("/new/:mode")
    @WithDataAdapter(VirtDB.class)
    public void showNewBalancer(VirtDB db, @SessionVar("currentAccount") Account currentAccount, String mode)
    {
        var("mode", mode);
        var("pools", db.listLoadBalancerPools());
        if ("https".equals(mode)) var("certificates", db.getACMECertificatesForAccount(currentAccount.getId()));
        encode("balancer/new");
    }
    
    @Post("/new/:mode")
    @WithDataAdapter(VirtDB.class)
    public void doNewBalancer(
            VirtDB db, 
            @SessionVar("currentAccount") Account currentAccount,
            String mode,
            @Param("name") String name, 
            @Param("description") String description, 
            @Param("pool") @IsaUUID UUID poolId,
            @Param("certificate") @IsaUUID UUID certificateId,
            @Param("redirectHttp") @IsaBoolean(defaultValue = false, coalesce = CoalesceMode.ON_ANY_ERROR) Boolean redirectHttp,
            @Param("port") @IsaInt Integer port,
            @Param("names") String names,
            @Param("healthCheckInterval") @IsaInt(mandatory = true, defaultValue = 30, coalesce = CoalesceMode.ALWAYS) Integer healthCheckInterval,
            @Param("healthCheckTimeout") @IsaInt(mandatory = true, defaultValue = 5, coalesce = CoalesceMode.ALWAYS) Integer healthCheckTimeout,
            @Param("healthCheckRise") @IsaInt(mandatory = true, defaultValue = 3, coalesce = CoalesceMode.ALWAYS) Integer healthCheckRise,
            @Param("healthCheckFall") @IsaInt(mandatory = true, defaultValue = 2, coalesce = CoalesceMode.ALWAYS) Integer healthCheckFall,
            @Param("healthCheckPath") @CheckStringLength(mandatory = true, min = 1, defaultValue = "/", coalesce = CoalesceMode.ALWAYS) String healthCheckPath,
            @Param("healthCheckStatus") @CheckStringLength(mandatory = true, min = 1, defaultValue = "200,3xx", coalesce = CoalesceMode.ALWAYS) String healthCheckStatus,
            @Param("healthCheckMode") @CheckStringLength() String healthCheckMode
    ) throws IOException
    {
        LoadBalancerPool pool = notNull(db.getLoadBalancerPool(poolId));
        // create the 
        LoadBalancer balancer = new LoadBalancer(currentAccount, pool, name, mode, description);
        if ("https".equals(mode))
        {
            if (certificateId != null)
            {
                ACMECertificate cert = notNull(db.getACMECertificate(certificateId));
                balancer.setCertificateId(cert.getId());
            }
            balancer.setRedirectHttp(redirectHttp);
        }
        if ("http".equals(mode) || "tls".equals(mode))
        {
            balancer.setDomains(Arrays.asList(names.split(",")).stream().map(String::trim).collect(Collectors.toList()));
        }
        balancer.setHealthCheckInterval(healthCheckInterval);
        balancer.setHealthCheckTimeout(healthCheckTimeout);
        balancer.setHealthCheckRise(healthCheckRise);
        balancer.setHealthCheckFall(healthCheckFall);
        if ("http".equals(mode) || "https".equals(mode))
        {
            balancer.setHealthCheckPath(healthCheckPath);
            balancer.setHealthCheckStatus(healthCheckStatus);
        }
        if ("tcp".equals(mode))
        {
            balancer.setHealthCheckMode(healthCheckMode);
        }
        else if ("tls".equals(mode) || "http".equals(mode) || "https".equals(mode))
        {
            balancer.setHealthCheckMode(mode);
        }
        db.execute(() ->{
            // Attempt to allocate a TCP Port for tcp balancers
            if ("tcp".equals(mode))
            {
                LoadBalancerPoolTCPPort allocatedPort = notNull(db.allocateLoadBalancerTCPPorts(pool.getId(), notNull(port).intValue()));
                balancer.setTcpPortId(allocatedPort.getId());
            }
            // create the LB
            db.setLoadBalancer(balancer);
        });
        // add DNS records for this load balancer
        deferredAction("balancer.add.dns.records", currentAccount, balancer);
        // request any certificates that are needed for the balancer generated names
        deferredAction("balancer.generate.certificate", currentAccount, balancer);
        redirect("/balancer/");
    }
}
