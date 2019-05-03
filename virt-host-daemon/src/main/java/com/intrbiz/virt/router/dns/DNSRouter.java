package com.intrbiz.virt.router.dns;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

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
import com.intrbiz.virt.model.DNSContent;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.DNSZone;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.dns.DNSResult;
import com.intrbiz.virt.model.dns.DNSResultSet;

@Prefix("/dns/")
public class DNSRouter extends Router<VirtHostApp>
{
    private Logger logger = Logger.getLogger(DNSRouter.class);
    
    private static final Pattern VALID_IPV4 = Pattern.compile("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}");
    
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
        // TODO: fix content length
        String response = result.toString();
        response().ok().json().header("Content-Length", String.valueOf(response.length())).write(response);
    }
    
    private void lookupSOA(VirtDB db, String name, DNSResultSet result)
    {
        if (app().getInternalZone().equals(name))
        {
            result.add(new DNSResult("SOA", name, "ns1." + app().getInternalZone() + " hostmaster." + app().getInternalZone() + " 1 7200 3600 86400 600", 7200));
        }
    }
    
    private void lookupNS(VirtDB db, String name, DNSResultSet result)
    {
        if (app().getInternalZone().equals(name))
        {
            result.add(new DNSResult("NS", name, "ns1." + app().getInternalZone(), 7200));
        }
    }
    
    private void lookupAny(VirtDB db, String name, String type, DNSResultSet result)
    {
        if ("ns1.intrbiz.cloud.".equals(name) && ("ANY".equals(type) || "A".equals(type)))
        {
            result.add(new DNSResult("A", name, app().getMetadataGateway(), 3600));
        }
        // Look up the name
        String internalZone = app().getInternalZone();
        if (name.endsWith(internalZone))
        {
            String zonelessName = name.substring(0, name.length() - internalZone.length());
            String[] parts = zonelessName.split("[.]");
            logger.info("Lookup: '" + zonelessName + "' -> " + Arrays.asList(parts));
            Account account = (parts.length >= 1) ? db.getAccountByName(parts[parts.length - 1]) : db.getAccountByName(app().getRootAccountName());
            logger.info("Looking up within account " + account.getId() + " " + account.getName());
            if (account != null)
            {
                String accountZoneName = account.getName() + "." + internalZone;
                if (parts.length == 2 && ("ANY".equals(type) || "A".equals(type)))
                {
                    this.lookupHost(db, accountZoneName, account, parts[0], result);
                }
                if (parts.length == 3 && ("ANY".equals(type) || "A".equals(type)))
                {
                    this.lookupHostInNetwork(db, accountZoneName, account, parts[0], parts[1], result);
                }
                this.lookupDNSRecords(db, internalZone, account, parts.length == 1 ? "@" : buildName(parts, parts.length - 1), type, result);
            }
        }
    }
    
    private void lookupHost(VirtDB db, String accountZoneName, Account account, String name, DNSResultSet result)
    {
        logger.info("Looking up Host: " + account.getId() + " " + name + " for " + accountZoneName);
        for (MachineNIC nic : db.lookupMachineNIC(account.getId(), name))
        {
            result.add(new DNSResult("A", name + "." + accountZoneName, nic.getIpv4(), 3600));
        }
    }
    
    private void lookupHostInNetwork(VirtDB db, String accountZoneName, Account account, String name, String networkName, DNSResultSet result)
    {
        logger.info("Looking up Host: " + account.getId() + " " + name + " in " + networkName + " for " + accountZoneName);
        for (MachineNIC nic : db.lookupMachineNICOnNetwork(account.getId(), name, networkName))
        {
            result.add(new DNSResult("A", name + "." + networkName + "." + accountZoneName, nic.getIpv4(), 3600));
        }
    }
    
    private void lookupDNSRecords(VirtDB db, String accountZoneName, Account account, String name, String type, DNSResultSet result)
    {
        logger.info("Looking up DNS Records: " + account.getId() + " " + name + "::" + type + " for " + accountZoneName);
        for (DNSRecord record : db.lookupDNSRecordsForAccount(account.getId(), DNSRecord.Scope.INTERNAL, type, name))
        {
            formatRecord(accountZoneName, name, record, result);
        }
    }
    
    private void formatRecord(String accountZoneName, String name, DNSContent record, DNSResultSet result)
    {
        switch (record.getType())
        {
            case "MX":
                result.add(new DNSResult(record.getType(), name, record.getPriority() + " " + record.getContent(), record.getTtl()));
                break;
            case "CNAME":
                result.add(new DNSResult(record.getType(), name, DNSZone.qualifyName(record.getContent(), record.getZoneName(accountZoneName)), record.getTtl()));
                break;
            case "A":
                // Ensure the IP address is valid
                if (VALID_IPV4.matcher(record.getContent()).matches())
                    result.add(new DNSResult(record.getType(), name, record.getContent(), record.getTtl()));    
                break;
            default:
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