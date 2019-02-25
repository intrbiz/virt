package com.intrbiz.virt.router.dns;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Before;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.virt.VirtHostApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.dns.DNSResult;
import com.intrbiz.virt.model.dns.DNSResultSet;

@Prefix("/dns/")
public class DNSRouter extends Router<VirtHostApp>
{   
    private Logger logger = Logger.getLogger(DNSRouter.class);
    
    private Set<String> zones = new HashSet<String>(Arrays.asList("intrbiz.cloud."));
    
    @Before
    @Any("/**")
    public void filterLocalRequest()
    {
        require("127.0.0.1".equals(request().getRemoteAddress()));
    }
    
    @Get("/lookup/:name/:type")
    @WithDataAdapter(VirtDB.class)
    public void lookup(VirtDB db, String name, String type) throws IOException
    {
        DNSResultSet result = new DNSResultSet();
        switch (type)
        {
            case "SOA": 
                this.lookupSOA(db, name, result);
                break;
            case "NS":
                this.lookupNS(db, name, result);
                break;
            default:
                this.lookupSOA(db, name, result);
                this.lookupNS(db, name, result);
                this.lookupAny(db, name, type, result);
        }
        // Send the result
        String response = result.toString();
        response().ok().json().header("Content-Length", String.valueOf(response.length())).write(response);
    }
    
    private void lookupSOA(VirtDB db, String name, DNSResultSet result)
    {
        if (this.zones.contains(name))
        {
            result.add(new DNSResult("SOA", name, "ns1.intrbiz.cloud. hostmaster.intrbiz.cloud. 1 7200 3600 86400 600", 7200));
        }
    }
    
    private void lookupNS(VirtDB db, String name, DNSResultSet result)
    {
        if (this.zones.contains(name))
        {
            result.add(new DNSResult("NS", name, "ns1.intrbiz.cloud.", 7200));
        }
    }
    
    private void lookupAny(VirtDB db, String name, String type, DNSResultSet result)
    {
        if ("ns1.intrbiz.cloud.".equals(name) && ("ANY".equals(type) || "A".equals(type)))
        {
            result.add(new DNSResult("A", name, app().getMetadataGateway(), 3600));
        }
        // Look up the name    
        String[] parts = name.split("[.]");
        logger.info("Lookup: " + Arrays.asList(parts));
        if (parts.length >= 3)
        {
            Account account = db.getAccountByName(parts[parts.length - 3]);
            if (account != null)
            {
                if (parts.length == 4 && ("ANY".equals(type) || "A".equals(type)))
                {
                    // Lookup a host
                    this.lookupHost(db, account, parts[0], result);
                }
                this.lookupDNSRecords(db, account, parts.length == 3 ? "@" : buildName(parts, parts.length - 3), type, result);
            }
            // TODO: assume a default account for prod services?
        }
    }
    
    private void lookupHost(VirtDB db, Account account, String name, DNSResultSet result)
    {
        logger.info("Looking up Host: " + account.getId() + " " + name);
        // Lookup the host in the account
        Machine machine = db.getMachineByName(account.getId(), name);
        if (machine != null)
        {
            MachineNIC mainNic = machine.getInterfaces().stream().findFirst().orElse(null);
            if (mainNic != null)
            {
                result.add(new DNSResult("A", name, mainNic.getIpv4(), 3600));
            }
        }
    }
    
    private void lookupDNSRecords(VirtDB db, Account account, String name, String type, DNSResultSet result)
    {
        logger.info("Looking up DNS Records: " + account.getId() + " " + name + "::" + type);
        for (DNSRecord record : db.lookupDNSRecordsForAccount(account.getId(), DNSRecord.Scope.INTERNAL, type, name))
        {
            // TODO: handle priority
            // TODO: handle @
            result.add(new DNSResult(record.getType(), name, record.getContent(), record.getTtl()));
        }
    }
    
    private String buildName(String[] parts, int toIndex)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < toIndex; i++)
        {
            if (i > 0) sb.append(".");
            sb.append(parts[i]);
        }
        return sb.toString();
    }
    
}