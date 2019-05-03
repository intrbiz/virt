package com.intrbiz.virt.dash.router.dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.intrbiz.Util;
import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Catch;
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
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.DNSZone;
import com.intrbiz.virt.model.DNSZoneRecord;

@Prefix("/dns")
@Template("layout/main")
@RequireValidPrincipal()
public class DNSRouter extends Router<VirtDashApp>
{
    @Get("/")
    @WithDataAdapter(VirtDB.class)
    public void listDNSRecords(VirtDB db, @SessionVar("currentAccount") Account currentAccount)
    {
        var("internal", db.getDNSRecordsForAccount(currentAccount.getId(), DNSRecord.Scope.INTERNAL));
        var("external", db.getDNSRecordsForAccount(currentAccount.getId(), DNSRecord.Scope.EXTERNAL));
        var("zones", db.getDNSZonesForAccount(currentAccount.getId()));
        var("hosted_domain", app().getHostedDomain());
        encode("dns/index");
    }
    
    @Post("/new/scope/:scope")
    @WithDataAdapter(VirtDB.class)
    public void addDNSRecord(
            VirtDB db,
            @SessionVar("currentAccount") Account currentAccount,
            String scope,
            @Param("type") String type,
            @Param("name") String name, 
            @Param("content") String content,
            @Param("ttl") @IsaInt(min = 30, max = 86400, mandatory = true, defaultValue = 3600, coalesce = CoalesceMode.ALWAYS) Integer ttl,
            @Param("priority") @IsaInt(mandatory = true, defaultValue = 0, coalesce = CoalesceMode.ALWAYS) Integer priority,
            @Param("alias") @IsaBoolean(mandatory = true, defaultValue = false, coalesce = CoalesceMode.ALWAYS) Boolean alias
    ) throws IOException
    {
        db.setDNSRecord(new DNSRecord(currentAccount, scope, type, name.trim(), content.trim(), ttl, priority, alias, false));
        redirect("/dns/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/new/scope/:scope")
    public void addDNSRecordError()
    {
        encode("dns/index");
    }
    
    @Get("/id/:id/remove")
    @WithDataAdapter(VirtDB.class)
    public void removeDNSRecord(VirtDB db, @SessionVar("currentAccount") Account currentAccount, @IsaUUID() UUID id) throws IOException
    {
        db.removeDNSRecord(id);
        redirect("/dns/");
    }
    
    @Get("/zone/new")
    public void showNewZone()
    {
        encode("dns/new-zone");
    }
    
    @Post("/zone/new")
    @WithDataAdapter(VirtDB.class)
    public void doNewZone(VirtDB db, @SessionVar("currentAccount") Account currentAccount, @Param("zoneName") String zoneName, @Param("aliases") String aliases) throws IOException
    {
        List<String> aliasList = Arrays.stream(Util.coalesce(aliases, "").split(",")).map(String::trim).collect(Collectors.toList());
        db.setDNSZone(new DNSZone(zoneName, aliasList, currentAccount));
        redirect("/dns/");
    }
    
    @Post("/new/zone/:id")
    @WithDataAdapter(VirtDB.class)
    public void addDNSZoneRecord(
            VirtDB db,
            @SessionVar("currentAccount") Account currentAccount,
            @IsaUUID UUID zoneId,
            @Param("type") String type,
            @Param("name") String name, 
            @Param("content") String content,
            @Param("ttl") @IsaInt(min = 30, max = 86400, mandatory = true, defaultValue = 3600, coalesce = CoalesceMode.ALWAYS) Integer ttl,
            @Param("priority") @IsaInt(mandatory = true, defaultValue = 0, coalesce = CoalesceMode.ALWAYS) Integer priority,
            @Param("alias") @IsaBoolean(mandatory = true, defaultValue = false, coalesce = CoalesceMode.ALWAYS) Boolean alias
    ) throws IOException
    {
        DNSZone zone = notNull(db.getDNSZone(zoneId));
        db.setDNSZoneRecord(new DNSZoneRecord(zone, type, name.trim(), content.trim(), ttl, priority, alias, false));
        redirect("/dns/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/new/zone/:id")
    public void addDNSZoneRecordError()
    {
        encode("dns/index");
    }
    
    @Get("/zone/record/id/:id/remove")
    @WithDataAdapter(VirtDB.class)
    public void removeDNSZoneRecord(VirtDB db, @SessionVar("currentAccount") Account currentAccount, @IsaUUID() UUID id) throws IOException
    {
        db.removeDNSZoneRecord(id);
        redirect("/dns/");
    }
}
