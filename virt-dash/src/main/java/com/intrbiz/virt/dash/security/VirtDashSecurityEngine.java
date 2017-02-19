package com.intrbiz.virt.dash.security;

import java.security.Principal;

import org.apache.log4j.Logger;

import com.intrbiz.Util;
import com.intrbiz.balsa.engine.impl.security.SecurityEngineImpl;
import com.intrbiz.balsa.error.BalsaSecurityException;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.dash.cfg.VirtDashUser;

public class VirtDashSecurityEngine extends SecurityEngineImpl
{
    private Logger logger = Logger.getLogger(VirtDashSecurityEngine.class);
    
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
        VirtDashCfg cfg = ((App) this.getBalsaApplication()).getConfig();
        VirtDashUser user = cfg.getUser(username);
        if (user != null)
        {
            // a blank password means we don't bother checking
            // this is a lazy way to make it easy to add users
            if (Util.isEmpty(user.getPasswordHash()))
            {
                logger.warn("Authenticated user " + user.getUsername() + " with blank password, please change the password for " + user.getUsername());
                return user;
            }
            // verify the password, erroring or returning the principal
            if (!user.verifyPassword(new String(password))) throw new BalsaSecurityException("Invalid password");
            return user;
        }
        throw new BalsaSecurityException("No such principal");
    }
}
