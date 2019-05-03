package com.intrbiz.virt.dash.router.register;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Config;
import com.intrbiz.virt.model.Role;
import com.intrbiz.virt.model.User;
import com.intrbiz.virt.model.UserAccountGrant;
import com.intrbiz.virt.util.NameUtil;

// Register Account
@Prefix("/account")
@Template("layout/single")
public class RegisterRouter extends Router<VirtDashApp>
{
    private static Logger logger = Logger.getLogger(RegisterRouter.class);
    
    private Map<String, String> defaultAccounts = new HashMap<>();
    
    public RegisterRouter()
    {
        this.defaultAccounts.put("dev-", "Development");
        this.defaultAccounts.put("", "Production");
    }
    
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
            @Param("organisation_name") @CheckStringLength(mandatory = true, min = 3, max = 100) String organisationName,
            @Param("organisation_slug") @CheckStringLength(mandatory = true, min = 3, max = 40) String organisationSlug,
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
        String accountSlug = NameUtil.toSafeName(organisationSlug);
        db.execute(() -> {
            // Create the User
            User user = new User(email, fullName, givenName, mobile);
            user.hashPassword(passwordNew);
            db.setUser(user);
            // Create the account
            Set<Account> accounts = new HashSet<>();
            for (Entry<String, String> prefix : this.defaultAccounts.entrySet())
            {
                Account account = new Account(prefix.getKey() + accountSlug, organisationName + " " + prefix.getValue());
                accounts.add(account);
                db.setAccount(account);
                // Grants
                UserAccountGrant grant = new UserAccountGrant(user.getId(), account.getId(), Role.ACCOUNT_OWNER);
                db.setUserAccountGrant(grant);
            }
            // First Install
            Config firstInstallComplete = db.getOrCreateConfig(Config.FIRST_INSTALL_COMPLETE);
            if (! firstInstallComplete.getBooleanValue())
            {
                // Make the user a super user
                user.setSuperuser(true);
                db.setUser(user);
                // Activate the accounts
                for (Account account : accounts)
                {
                    account.setActive(true);
                    db.setAccount(account);
                }
                // Finish first install
                firstInstallComplete.setBooleanValue(true);
                db.setConfig(firstInstallComplete);
            }
        });
        // redirect to login
        redirect("/login");
    }
    
    
}
