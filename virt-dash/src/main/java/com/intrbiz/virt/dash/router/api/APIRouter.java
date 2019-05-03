package com.intrbiz.virt.dash.router.api;

import java.io.IOException;
import java.util.Base64;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaSecurityException;
import com.intrbiz.balsa.http.HTTP.HTTPStatus;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Before;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CurrentPrincipal;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Order;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.User;

@Prefix("/api")
public class APIRouter extends Router<VirtDashApp>
{
    @Before
    @Any("/**")
    @WithDataAdapter(VirtDB.class)
    public void authenticateAPI(VirtDB db)
    {
        // Do we already have a user
        if (balsa().currentPrincipal() == null)
        {
            // Try Basic Auth
            String authorization = request().getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Basic"))
            {
                String[] credentials = new String(Base64.getDecoder().decode(authorization.substring(6))).split("[:]");
                this.authenticateRequest(credentials[0], credentials[1]);
            }
        }
    }
    
    @Get("/hello")
    @JSON
    @RequireValidPrincipal()
    public String hello(@CurrentPrincipal User user)
    {
        return "Hello " + user.getGivenName();
    }
    
    @Catch(BalsaSecurityException.class)
    @Order(Order.LAST - 10)
    @Any("/**")
    @JSON(status = HTTPStatus.Forbidden)
    public String apiAuthError() throws IOException
    {
        return "Access Denied";
    }
    
    @Catch()
    @Order(Order.LAST)
    @Any("/**")
    @JSON(status = HTTPStatus.InternalServerError)
    public String apiError() throws IOException
    {
        Throwable t = balsa().getException();
        if (t != null) t.printStackTrace();
        return "Oops: " + t;
    }
}
