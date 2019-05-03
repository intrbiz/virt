package com.intrbiz.virt.dash.router.api;

import java.util.List;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.CurrentPrincipal;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.User;

@Prefix("/api/dns")
@RequireValidPrincipal()
public class DNSAPIRouter extends Router<VirtDashApp>
{    
    @Get("/account/name/:name/scope/:scope")
    @JSON
    @WithDataAdapter(VirtDB.class)
    public List<DNSRecord> listDNSRecords(VirtDB db, @CurrentPrincipal User user, String accountName, String scope)
    {
        Account account = notNull(user.getOwnedAccounts().stream().filter(a -> a.getName().equals(accountName)).findFirst().orElse(null));
        return db.getDNSRecordsForAccount(account.getId(), scope);
    }
    
    @Get("/account/name/:name/scope/:scope/type/:type")
    @JSON
    @WithDataAdapter(VirtDB.class)
    public List<DNSRecord> listDNSRecordsOfType(VirtDB db, @CurrentPrincipal User user, String accountName, String scope, String type)
    {
        Account account = notNull(user.getOwnedAccounts().stream().filter(a -> a.getName().equals(accountName)).findFirst().orElse(null));
        return db.getDNSRecordsForAccount(account.getId(), scope, type);
    }
}
