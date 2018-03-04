package com.intrbiz.virt.dash.router;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidAccessTokenForURL;
import com.intrbiz.metadata.Template;
import com.intrbiz.validator.ValidationException;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Config;
import com.intrbiz.virt.model.Role;
import com.intrbiz.virt.model.User;
import com.intrbiz.virt.model.UserAccountGrant;

// Register Account
@Prefix("/account")
@Template("layout/single")
public class RegisterRouter extends Router<App>
{
    private static Logger logger = Logger.getLogger(RegisterRouter.class);
    
    @Get("/register")
    public void register()
    {
        encode("account/register");
    }
    
    @Post("/register")
    @RequireValidAccessTokenForURL()
    @WithDataAdapter(VirtDB.class)
    public void register(
            VirtDB db, 
            @Param("full_name") @CheckStringLength(mandatory = true, max = 100) String fullName, 
            @Param("given_name") @CheckStringLength(mandatory = true, max = 30) String givenName,
            @Param("email") @CheckStringLength(mandatory = true, max = 200) String email, 
            @Param("mobile") String mobile,
            @Param("password_new") @CheckStringLength(mandatory = true, max = 100) String passwordNew, 
            @Param("password_confirm") @CheckStringLength(mandatory = true, max = 100) String passwordConfirm
    ) throws IOException
    {
        if (! passwordNew.equals(passwordConfirm))
            throw new ValidationException("Password does not match");
        if (db.getUserByName(email) != null)
            throw new ValidationException("Account already exists");
        logger.info("Registering user: " + email + " " + fullName);
        db.execute(() -> {
            // Create the User
            User user = new User(email, fullName, givenName, mobile);
            user.hashPassword(passwordNew);
            db.setUser(user);
            // Create the account
            Account account = new Account("Production");
            db.setAccount(account);
            // Grants
            UserAccountGrant grant = new UserAccountGrant(user.getId(), account.getId(), Role.ACCOUNT_OWNER);
            db.setUserAccountGrant(grant);
            // First Install
            Config firstInstallComplete = db.getOrCreateConfig(Config.FIRST_INSTALL_COMPLETE);
            if (! firstInstallComplete.getBooleanValue())
            {
                account.setActive(true);
                user.setSuperuser(true);
                firstInstallComplete.setBooleanValue(true);
                db.setAccount(account);
                db.setUser(user);
                db.setConfig(firstInstallComplete);
            }
        });
        // redirect to login
        redirect("/login");
    }
}
