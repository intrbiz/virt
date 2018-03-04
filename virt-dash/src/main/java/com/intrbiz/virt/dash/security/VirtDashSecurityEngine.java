package com.intrbiz.virt.dash.security;

import java.security.Principal;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.engine.impl.security.SecurityEngineImpl;
import com.intrbiz.balsa.error.BalsaSecurityException;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Permission;
import com.intrbiz.virt.model.User;
import com.intrbiz.virt.model.UserAccountGrant;

public class VirtDashSecurityEngine extends SecurityEngineImpl
{
    private static Logger logger = Logger.getLogger(VirtDashSecurityEngine.class);
    
    public VirtDashSecurityEngine()
    {
        super();
    }

    @Override
    public String getEngineName()
    {
        return "Virt Dash Security Engine";
    }

    @Override
    public Principal doPasswordLogin(String username, char[] password) throws BalsaSecurityException
    {
        logger.info("Password authentication for user: " + username);
        try (VirtDB db = VirtDB.connect())
        {
            User user = db.getUserByName(username);
            if (user != null && user.verifyPassword(new String(password)))
            {
                return user;
            }
        }
        throw new BalsaSecurityException("No such principal");
    }

    @Override
    public boolean check(Principal principal, String permission)
    {
        return ((User) principal).isSuperuser();
    }

    @Override
    public boolean check(Principal principal, String permission, Object object)
    {
        // map the object to the account
        UUID accountId = null;
        if (object instanceof UUID) accountId = (UUID) object;
        else if (object instanceof Account) accountId = ((Account) object).getId();
        // check the permission on the account
        Permission perm = Permission.fromString(permission);
        User user = (User) principal;
        for (UserAccountGrant grant : user.getAccountGrants())
        {
            if (grant.getAccountId().equals(accountId) && grant.hasPermission(perm))
                return true;
        }
        return false;
    }
    
    
}
