package com.intrbiz.virt.dash.router.internal;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.ACMEWellKnown;

@Prefix("/internal/acme")
public class ACMEWellKnownRouter extends InternalRouter
{
    private static final Logger logger = Logger.getLogger(ACMEWellKnownRouter.class);
    
    @Get("/:host/.well-known/acme-challenge/:name")
    @WithDataAdapter(VirtDB.class)
    @Text()
    public String lookupZones(VirtDB db, String host, String name) throws IOException
    {
        logger.info("Looking up ACME well known challenge for host " + host + "/" + name + " - " + request().getServerName());
        ACMEWellKnown wellKnown = db.getACMEWellKnown(host, name);
        return wellKnown == null ? "" : wellKnown.getChallenge();
    }
}

