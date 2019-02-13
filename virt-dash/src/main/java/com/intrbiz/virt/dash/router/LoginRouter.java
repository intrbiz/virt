package com.intrbiz.virt.dash.router;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.intrbiz.Util;
import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaSecurityException;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Order;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidAccessTokenForURL;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.User;

@Prefix("/")
public class LoginRouter extends Router<VirtDashApp>
{
    private static final Logger logger = Logger.getLogger(LoginRouter.class);
    
    @Get("/login")
    public void login(@Param("redirect") String redirect)
    {
        var("redirect", redirect);
        encode("layout/single", "login");
    }

    @Post("/login")
    @RequireValidAccessTokenForURL()
    public void doLogin(@Param("username") String username, @Param("password") String password, @Param("redirect") String redirect) throws IOException
    {
        User currentUser = authenticate(username, password);
        // setup the currently active account
        sessionVar("currentAccount", currentUser.getAccounts().iterator().next());
        // force a password change?
        if (currentUser.isPasswordChange())
        {
            redirect(path("/change-password"));
        }
        else
        {
            redirect(path(Util.isEmpty(redirect) ? "/" : redirect));
        }
    }

    @Get("/logout")
    public void logout() throws IOException
    {
        deauthenticate();
        redirect(path("/login"));
    }
    
    @Get("/change-password")
    @RequireValidPrincipal()
    public void changePassword() throws IOException
    {
        encode("layout/main", "change_password");
    }
    
    @Post("/change-password")
    @RequireValidPrincipal()
    @RequireValidAccessTokenForURL()
    @WithDataAdapter(VirtDB.class)
    public void changePassword(VirtDB db, @Param("password_new") String passwordNew, @Param("password_confirm") String passwordConfirm) throws IOException
    {
        if (passwordNew != null && passwordNew.length() > 4 && passwordNew.equals(passwordConfirm))
        {
            User user = currentPrincipal();
            logger.info("Password change for user: " + user.getName());
            user.hashPassword(passwordNew);
            db.setUser(user);
            redirect(path("/"));
        }
        else
        {
            this.changePassword();   
        }
    }
    
    @Catch(BalsaSecurityException.class)
    @Order(Order.LAST)
    @Any("/**")
    public void forceLogin(@Param("redirect") String redirect) throws IOException
    {
        String to = Util.isEmpty(redirect) ? request().getPathInfo() : redirect;
        redirect(path("/login?redirect=" + Util.urlEncode(to, Util.UTF8)));
    }
}
