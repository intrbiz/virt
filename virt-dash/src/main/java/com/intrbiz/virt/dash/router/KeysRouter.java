package com.intrbiz.virt.dash.router;

import java.io.IOException;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.SessionVar;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.SSHKey;

@Prefix("/keys")
@Template("layout/main")
@RequireValidPrincipal()
public class KeysRouter extends Router<VirtDashApp>
{
    @Get("/")
    @WithDataAdapter(VirtDB.class)
    public void sshKeys(VirtDB db, @SessionVar("currentAccount") Account currentAccount)
    {
        var("keys", db.getSSHKeysForAccount(currentAccount.getId()));
        encode("keys/index");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void requestAccount(
            VirtDB db,
            @SessionVar("currentAccount") Account currentAccount,
            @Param("name") @CheckStringLength(mandatory = true, min = 3) String name,
            @Param("key") @CheckStringLength(mandatory = true, min = 50) String keyLines
    ) throws IOException
    {
        String[] keys = keyLines.split("(?:\r\n|\n|\r)");
        db.setSSHKey(new SSHKey(currentAccount.getId(), name, keys));
        redirect("/keys/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/new")
    public void requestAccountError()
    {
        encode("keys/index");
    }
}
