package com.intrbiz.virt.dash.router.dashboard;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.intrbiz.Util;
import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.CurrentPrincipal;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Role;
import com.intrbiz.virt.model.User;
import com.intrbiz.virt.model.UserAccountGrant;

@Prefix("/profile")
@Template("layout/main")
@RequireValidPrincipal()
public class ProfileRouter extends Router<VirtDashApp>
{
    private static final Logger logger = Logger.getLogger(ProfileRouter.class);
    
    @Any("/")
    public void index()
    {
        User user = currentPrincipal();
        var("user", user);
        var("accounts", user.getOwnedAccounts());
        encode("profile/index");
    }
    
    @Any("/account/switch/id/:id")
    public void switchAccount(@IsaUUID UUID id, @Param("redirect") String redirect) throws IOException
    {
        User user = currentPrincipal();
        sessionVar("currentAccount", notNull(user.getAccounts().stream().filter((a) -> a.getId().equals(id)).findFirst().orElse(null)));
        redirect(Util.coalesceEmpty(redirect, "/"));
    }
    
    @Get("/account/request")
    public void requestAccount()
    {
        encode("profile/request-account");
    }
    
    @Post("/account/request")
    @WithDataAdapter(VirtDB.class)
    public void requestAccount(
            VirtDB db,
            @Param("name") @CheckStringLength(mandatory = true, min=3, max = 100) String name,
            @Param("slug") @CheckStringLength(mandatory = true, min=3, max = 40) String slug,
            @Param("reason") String reason,
            @CurrentPrincipal User user
    ) throws IOException
    {
        logger.info("Requesting account for " + user + " " + name + "(" + slug + ") because " + reason);
        db.execute(() -> {
            Account account = new Account(slug, name);
            db.setAccount(account);
            UserAccountGrant grant = new UserAccountGrant(user.getId(), account.getId(), Role.ACCOUNT_OWNER);
            db.setUserAccountGrant(grant);
        });
        redirect("/profile/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/account/request")
    public void requestAccountError()
    {
        encode("profile/request-account");
    }
}
