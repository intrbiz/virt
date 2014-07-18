package com.intrbiz.virt.dash.router;

import java.io.IOException;

import com.intrbiz.Util;
import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaSecurityException;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Order;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidAccessTokenForURL;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.dash.cfg.VirtDashUser;

@Prefix("/")
public class LoginRouter extends Router<App>
{
    @Get("/login")
    public void login(@Param("redirect") String redirect)
    {
        model("redirect", redirect);
        encodeOnly("login");
    }

    @Post("/login")
    @RequireValidAccessTokenForURL()
    public void doLogin(@Param("username") String username, @Param("password") String password, @Param("redirect") String redirect) throws IOException
    {
        System.out.println("Login: " + username);
        VirtDashUser currentUser = (VirtDashUser) authenticate(username, password);
        // force a password change?
        if (Util.isEmpty(currentUser.getPasswordHash()))
            redirect(path("/change-password"));
        else
            redirect(path(Util.isEmpty(redirect) ? "/" : redirect));
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
    public void changePassword(@Param("password_new") String passwordNew, @Param("password_confirm") String passwordConfirm) throws IOException
    {
        VirtDashUser currentUser = (VirtDashUser) balsa().currentPrincipal();
        if (passwordNew.equals(passwordConfirm))
        {
            currentUser.hashPassword(passwordNew);
            ((App) this.app()).writeConfig();
        }
        redirect(path("/"));
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
