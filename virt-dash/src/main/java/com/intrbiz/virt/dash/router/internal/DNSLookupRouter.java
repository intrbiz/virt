package com.intrbiz.virt.dash.router.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intrbiz.Util;
import com.intrbiz.balsa.http.HTTP.HTTPStatus;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Order;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.virt.dash.cfg.HostedDNSCfg;
import com.intrbiz.virt.dash.model.dns.DNSResult;
import com.intrbiz.virt.dash.model.dns.DNSResultSet;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.DNSContent;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.DNSZone;
import com.intrbiz.virt.model.DNSZoneRecord;

@Prefix("/internal/dns")
public class DNSLookupRouter extends InternalRouter
{
    private static final int MAX_ALIAS_DEPTH = 3;
    
    private static final Pattern VALID_IPV4 = Pattern.compile("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}");
    
    private final String domain;
    
    private final String hostMaster;
    
    private final List<String> nameServers = new ArrayList<String>();
    
    private final String primaryNameServer;
    
    private final Pattern accountDomainPattern;
    
    public DNSLookupRouter(HostedDNSCfg config)
    {
        super();
        this.domain = DNSZone.qualifyZoneName(config.getDomain());
        this.hostMaster = config.getHostMaster().replace('@', '.');
        for (String ns : config.getNameservers())
        {
            this.nameServers.add(DNSZone.qualifyZoneName(ns));
        }
        this.primaryNameServer = this.nameServers.get(0);
        this.accountDomainPattern = Pattern.compile("\\A(.*?)[.]??([^.]++)[.]" + this.domain.replace(".", "[.]") + "\\z");
    }
    
    @Get("/lookup/SOA")
    @WithDataAdapter(VirtDB.class)
    @JSON()
    public DNSResultSet lookupZones(VirtDB db) throws IOException
    {
        DNSResultSet result = new DNSResultSet();
        for (DNSZone zone : db.listDNSZones())
        {
            result.add(new DNSResult("SOA", zone.getZoneName(), this.primaryNameServer + " " + this.hostMaster + " 1 7200 3600 86400 600", 7200));
            for (String alias : zone.getAliases())
            {
                result.add(new DNSResult("SOA", alias, this.primaryNameServer + " " + this.hostMaster + " 1 7200 3600 86400 600", 7200));
            }
        }
        return result;
    }

    @Get("/lookup/:name/:type")
    @WithDataAdapter(VirtDB.class)
    @JSON()
    public DNSResultSet lookup(VirtDB db, String name, String type) throws IOException
    {
        type = type.toUpperCase();
        if (! name.endsWith(".")) name = name + ".";
        String searchName = name.toLowerCase();
        // Lookup
        DNSResultSet result = new DNSResultSet();
        switch (type)
        {
            case "SOA": 
                this.lookupZone(db, searchName, name, type, result);
                break;
            case "NS":
                this.lookupZone(db, searchName, name, type, result);
                break;
            default:
                this.lookupZone(db, searchName, name, type, result);
                this.lookupAny(db, searchName, 0, name, type, result);
        }
        return result;
    }
    
    private void lookupZone(VirtDB db, String searchName, String name, String type, DNSResultSet result)
    {
        DNSZone zone = db.getDNSZoneByName(searchName);
        if (zone != null)
        {
            if ("SOA".equals(type) || "ANY".equals(type))
            {
                result.add(new DNSResult("SOA", name,  this.primaryNameServer + " " + this.hostMaster + " 1 7200 3600 86400 600", 7200));
            }
            if ("NS".equals(type) || "ANY".equals(type))
            {
                for (String nameServer : this.nameServers)
                {
                    result.add(new DNSResult("NS", name, nameServer, 7200));
                }
            }
        }
    }
    
    private void lookupAny(VirtDB db, String searchName, int depth, String name, String type, DNSResultSet result)
    {
        for (DNSZoneRecord record : db.lookupDNSZoneRecords(type, searchName))
        {
            if (record.isAlias() && depth < MAX_ALIAS_DEPTH)
            {
                this.lookupAny(db, DNSZone.qualifyName(record.getContent(), record.getZoneName(this.domain)), depth + 1, name, record.getType(), result);
            }
            else
            {
                this.formatRecord(name, record, result);
            }
        }
        // look up format of account.intrbiz.cloud
        Matcher matcher = this.accountDomainPattern.matcher(searchName);
        if (matcher.matches())
        {
            String accountName = matcher.group(2);
            Account account = db.getAccountByName(accountName);
            if (account != null)
            {
                String subName = Util.coalesceEmpty(matcher.group(1), "@");
                for (DNSRecord record : db.lookupDNSRecordsForAccount(account.getId(), DNSRecord.Scope.EXTERNAL, type, subName))
                {
                    if (record.isAlias() && depth < MAX_ALIAS_DEPTH)
                    {
                        this.lookupAny(db, DNSZone.qualifyName(record.getContent(), record.getZoneName(this.domain)), depth + 1, name, record.getType(), result);
                    }
                    else
                    {
                        this.formatRecord(name, record, result);
                    }
                }
            }
        }
    }
    
    private void formatRecord(String name, DNSContent record, DNSResultSet result)
    {
        switch (record.getType())
        {
            case "MX":
                result.add(new DNSResult(record.getType(), name, record.getPriority() + " " + record.getContent(), record.getTtl()));
                break;
            case "CNAME":
                result.add(new DNSResult(record.getType(), name, DNSZone.qualifyName(record.getContent(), record.getZoneName(this.domain)), record.getTtl()));
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
    
    @Get("/**")
    @Order(Order.LAST)
    @JSON(status = HTTPStatus.NotFound)
    public DNSResultSet calculateSOASerial() throws IOException
    {
        return DNSResultSet.NULL;
    }
}

