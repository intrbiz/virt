package com.intrbiz.virt.dash.router.admin;

import java.io.IOException;
import java.util.UUID;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequirePermission;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;

@Prefix("/admin/account")
@Template("layout/main")
@RequireValidPrincipal()
@RequirePermission("global_admin")
public class AccountsRouter extends Router<App>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void images(VirtDB db)
    {
        var("accounts", db.listAccounts());
        encode("admin/accounts");
    }
    
    @Any("/id/:id/active")
    @WithDataAdapter(VirtDB.class)
    public void makeAccountActive(VirtDB db, @IsaUUID UUID id) throws IOException
    {
        Account account = notNull(db.getAccount(id));
        account.setActive(true);
        db.setAccount(account);
        redirect("/admin/account/");
    }
}
