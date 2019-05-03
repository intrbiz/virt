package com.intrbiz.virt.dash.router.internal;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.virt.dash.model.balancer.LoadBalancerConfiguration;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.LoadBalancerPool;

@Prefix("/internal/balancer")
public class LoadBalancerConfigRouter extends InternalRouter
{
    private static final Logger logger = Logger.getLogger(LoadBalancerConfigRouter.class);
    
    @Get("/pool/id/:id/node/:nodeName")
    @WithDataAdapter(VirtDB.class)
    @JSON()
    public LoadBalancerConfiguration lookupZones(VirtDB db, @IsaUUID UUID poolId, String nodeName) throws IOException
    {
        LoadBalancerPool pool = notNull(db.getLoadBalancerPool(poolId));
        logger.info("Building Load Balancer configuration for pool " + pool.getName() + " as requested by node " + nodeName);
        LoadBalancerConfiguration config = new LoadBalancerConfiguration(pool, app().getHostedDomain(), nodeName);
        config.setLoadBalancers(db.getLoadBalancersForLoadBalancerPool(poolId));
        return config;
    }
}

