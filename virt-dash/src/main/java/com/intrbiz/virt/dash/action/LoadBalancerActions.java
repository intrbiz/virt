package com.intrbiz.virt.dash.action;

import static com.intrbiz.balsa.BalsaContext.*;

import java.util.Arrays;

import com.intrbiz.metadata.Action;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.ACMECertificate;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.DNSRecord.Scope;
import com.intrbiz.virt.model.LoadBalancer;
import com.intrbiz.virt.model.LoadBalancerPool;
import com.intrbiz.virt.model.LoadBalancerPoolTCPPort;

public class LoadBalancerActions extends ClusteredAction
{    
    public LoadBalancerActions()
    {
        super();
    }
    
    @Action("balancer.add.dns.records")
    public void addDNSRecordsForLoadBalancer(Account currentAccount, LoadBalancer balancer)
    {
        try (VirtDB db = VirtDB.connect())
        {
            if ("https".equals(balancer.getMode()) || "http".equals(balancer.getMode()) || "tls".equals(balancer.getMode()))
            {
                // Add the generated DNS records for the load balancer name
                LoadBalancerPool pool = balancer.getPool();
                db.setDNSRecord(new DNSRecord(currentAccount, Scope.EXTERNAL, "A", balancer.getName(), pool.getEndpoint(), 3600, 0, true, true));
            }
            else if ("tcp".equals(balancer.getMode()))
            {
                // Add the generated DNS records for the load balancer tcp port
                LoadBalancerPoolTCPPort port = balancer.getTcpPort();
                db.setDNSRecord(new DNSRecord(currentAccount, Scope.EXTERNAL, "A", balancer.getName(), port.getEndpoint(), 3600, 0, true, true));
            }
        }
    }

    @Action("balancer.generate.certificate")
    public void generateCertificateForLoadBalancer(Account currentAccount, LoadBalancer balancer) throws Exception
    {
        if ("https".equals(balancer.getMode()))
        {
            String balancerName = balancer.getName() + "." + currentAccount.getName() + "." + ((VirtDashApp) Balsa().app()).getHostedDomain();
            // Create the certificate
            ACMECertificate certificate = Balsa().action("acme.certificate.create", currentAccount.getId(), Arrays.asList(balancerName), true);
            // Update the load balancer with the generated certificate
            try (VirtDB db = VirtDB.connect())
            {
                db.execute(() -> {
                    LoadBalancer lb = db.getLoadBalancer(balancer.getId());
                    lb.setGeneratedCertificateId(certificate.getId());
                    db.setLoadBalancer(lb);
                });
            }
            // Issue the certificate
            Balsa().action("acme.certificate.issue", certificate);
        }
    }
}
