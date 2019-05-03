package com.intrbiz.virt.dash.router.dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.Util;
import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.SessionVar;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.ACMECertificate;
import com.intrbiz.virt.model.Account;

@Prefix("/certificate")
@Template("layout/main")
@RequireValidPrincipal()
public class CertificateRouter extends Router<VirtDashApp>
{   
    private static final Logger logger = Logger.getLogger(CertificateRouter.class);
    
    @Get("/")
    @WithDataAdapter(VirtDB.class)
    public void listCertificates(VirtDB db, @SessionVar("currentAccount") Account currentAccount)
    {
        var("certificates", db.getACMECertificatesForAccount(currentAccount.getId()));
        encode("certificate/index");
    }
    
    @Get("/id/:id")
    @WithDataAdapter(VirtDB.class)
    public void showCertificate(VirtDB db, @IsaUUID UUID id)
    {
        var("certificate", db.getACMECertificate(id));
        encode("certificate/details");
    }
    
    @Get("/id/:id/revoke")
    @WithDataAdapter(VirtDB.class)
    public void revokeCertificate(VirtDB db, @IsaUUID UUID id) throws IOException
    {
        db.removeACMECertificate(id);
        redirect("/certificate/");
    }
    
    @Get("/request")
    public void showRequestCertificate()
    {
        encode("certificate/request");
    }
    
    @Post("/request")
    @WithDataAdapter(VirtDB.class)
    public void doRequestCertificate(VirtDB db, @SessionVar("currentAccount") Account currentAccount, @Param("names") String names) throws IOException
    {
        // Create the certificate now
        List<String> namesList = Arrays.stream(Util.coalesce(names, "").split(",")).map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
        ACMECertificate certificate = action("acme.certificate.create", currentAccount.getId(), namesList, false);
        // Issue the certificate in the background
        deferredAction("acme.certificate.issue", certificate);
        redirect("/certificate/");
    }
    
    @Get("/id/:id/reissue")
    @WithDataAdapter(VirtDB.class)
    public void doReissueCertificate(VirtDB db, @IsaUUID UUID id) throws IOException
    {
        // Issue the certificate in the background
        ACMECertificate certificate = notNull(db.getACMECertificate(id));
        logger.info("Reissuing ACME certificate " + certificate.getId() + " for domains " + certificate.getDomains());
        deferredAction("acme.certificate.issue", certificate);
        redirect("/certificate/");
    }

}
