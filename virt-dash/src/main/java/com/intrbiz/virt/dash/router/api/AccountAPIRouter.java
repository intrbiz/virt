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
import com.intrbiz.virt.model.User;

@Prefix("/api/account/")
@RequireValidPrincipal()
public class AccountAPIRouter extends Router<VirtDashApp>
{    
    @Get("/")
    @JSON
    @WithDataAdapter(VirtDB.class)
    public List<Account> listAccounts(VirtDB db, @CurrentPrincipal User user)
    {
        return user.getOwnedAccounts();
    }
}
