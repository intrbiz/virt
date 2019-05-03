package com.intrbiz.virt.dash.action;

import java.io.IOException;
import java.security.KeyPair;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import com.intrbiz.metadata.Action;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.ACMEAccount;
import com.intrbiz.virt.model.ACMECertificate;
import com.intrbiz.virt.model.ACMEWellKnown;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.DNSZone;
import com.intrbiz.virt.model.DNSZoneRecord;

public class CertificateActions extends ClusteredAction
{
    private static final Logger logger = Logger.getLogger(CertificateActions.class);
    
    private static final int KEY_PAIR_SIZE = 2048;
    
    private final String acmeHost;
    
    private final String hostedDomain;
    
    public CertificateActions(String acmeHost, String hostedDomain)
    {
        this.acmeHost = acmeHost;
        this.hostedDomain = hostedDomain;
    }    
    
    public String getAcmeHost()
    {
        return acmeHost;
    }
    
    @Action("certificate.create.keypair")
    public KeyPair createKeyPair()
    {
        return KeyPairUtils.createKeyPair(KEY_PAIR_SIZE);
    }

    @Action("acme.create.account")
    public ACMEAccount createAcmeAccount(UUID currentAccountId) throws AcmeException
    {
        ACMEAccount acmeAccount = null;
        try (VirtDB db = VirtDB.connect())
        {
            acmeAccount = this.findAcmeAccount(db, currentAccountId);
        }
        return acmeAccount;
    }
    
    @Action("acme.certificate.create")
    public ACMECertificate createCertificate(UUID currentAccountId, List<String> namesList, boolean generated) throws AcmeException, IOException
    {
        // Create the ACME Account
        ACMEAccount acmeAccount = this.createAcmeAccount(currentAccountId);
        // Create the certificate that we want to order
        ACMECertificate certificate = new ACMECertificate(currentAccountId, acmeAccount, this.createKeyPair(), namesList, generated);
        try (VirtDB db = VirtDB.connect())
        {
            db.setACMECertificate(certificate);
            logger.info("Created ACME certificate " + certificate.getId() + " for domains " + namesList);
        }
        return certificate;
    }
    
    @Action("acme.certificate.request")
    public ACMECertificate requestCertificate(UUID currentAccountId, List<String> namesList, boolean generated) throws AcmeException, IOException
    {
        return this.issueCertificate(this.createCertificate(currentAccountId, namesList, generated));
    }
    
    @Action("acme.certificate.issue")
    public ACMECertificate issueCertificate(ACMECertificate certificate) throws AcmeException, IOException
    {
        // Load the keypair for this certificate
        KeyPair keyPair = certificate.loadKeyPair();
        // get the acme account
        ACMEAccount acmeAccount = certificate.getAcmeAccount();
        // now order the certificate
        Session session = this.createAcmeSession();
        Account account = this.createAcmeAccount(acmeAccount, session);
        Order order = account.newOrder().domains(certificate.getDomains()).create();
        // authorize the order
        for (Authorization auth : order.getAuthorizations())
        {
            if (! this.authorize(certificate.getAccountId(), auth))
            {
                // TODO, error logging
                logger.info("Failed to authorize certificate");
                return certificate;
            }
        }
        // build the CSR
        CSRBuilder csrb = new CSRBuilder();
        csrb.addDomains(certificate.getDomains());
        csrb.sign(keyPair);
        certificate.saveRequest(csrb);
        try (VirtDB db = VirtDB.connect())
        {
            db.setACMECertificate(certificate);
        }
        // complete the order
        logger.info("Ordering certificate " + certificate.getId());
        order.execute(csrb.getEncoded());
        // poll for the certificate to be issued
        for (int attempts = 0; order.getStatus() != Status.VALID && attempts < 30; attempts++)
        {
            pollWait(15_000L);
            order.update();
        }
        // yay all done
        if (order.getStatus() == Status.VALID)
        {
            certificate.issued(order.getCertificate());
            try (VirtDB db = VirtDB.connect())
            {
                db.setACMECertificate(certificate);
            }
            logger.info("Sucessfully ordered certificate " + certificate.getId());
        }
        else
        {
            logger.info("Failed to order certificate " + certificate.getId() + " " + order.getStatus());
        }
        return certificate;
    }
    
    private boolean authorize(UUID accountId, Authorization auth) throws AcmeException
    {
        logger.info("Authorization for domain " + auth.getIdentifier().getDomain() + " " + auth.getStatus());
        if (auth.getStatus() == Status.VALID)
        {
            return true;
        }
        // Is it for the hosted zone
        if (auth.getIdentifier().getDomain().endsWith(this.hostedDomain))
        {
            return this.authorizeViaHostedDNS(accountId, auth);
        }
        // Do we manage the zone
        DNSZone zone = this.findDNSZone(accountId, auth);
        if (zone != null)
        {
            return this.authorizeViaZonedDNS(accountId, zone, auth);
        }
        // Fall back to HTTP auth
        return this.authorizeViaHTTP(accountId, auth);
    }
    
    private boolean authorizeViaHTTP(UUID accountId, Authorization auth) throws AcmeException
    {
        // Find the DNS challenge
        Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
        if (challenge != null && challenge.getStatus() != Status.VALID)
        {
            logger.info("Setting up HTTP challenge " + auth.getIdentifier().getDomain() + " with challenge " + challenge.getToken() + " ==> " + challenge.getAuthorization());
            ACMEWellKnown record = null;
            try
            {
                try (VirtDB db = VirtDB.connect())
                {
                    record = new ACMEWellKnown(auth.getIdentifier().getDomain(), challenge.getToken(), challenge.getAuthorization());
                    db.setACMEWellKnown(record);
                }
                logger.info("Triggering HTTP challenge for " + auth.getIdentifier().getDomain());
                // trigger the challenge
                challenge.trigger();
                // poll the challenge
                for (int attempts = 0; challenge.getStatus() != Status.VALID && attempts < 30; attempts++)
                {
                    if (attempts > 5 && challenge.getStatus() == Status.INVALID)
                    {
                        logger.info("HTTP challenge failed for " + auth.getIdentifier().getDomain() + " with " + challenge.getStatus() + " " + challenge.getError());
                        return false;
                    }
                    pollWait(1_500L + (attempts * 5_000L));
                    challenge.update();
                }
            }
            finally
            {
                // clean up
                if (record != null)
                {
                    try (VirtDB db = VirtDB.connect())
                    {
                        db.removeACMEWellKnown(record.getHost(), record.getName());
                    }
                }
            }
            logger.info("HTTP challenge complete " + challenge.getStatus());
            return challenge.getStatus() == Status.VALID;
        }
        return false;
    }
    
    private boolean authorizeViaZonedDNS(UUID accountId, DNSZone zone, Authorization auth) throws AcmeException
    {
        // Find the DNS challenge
        Dns01Challenge challenge = auth.findChallenge(Dns01Challenge.TYPE);
        if (challenge != null && challenge.getStatus() != Status.VALID)
        {
            logger.info("Setting up Zoned DNS challenge " + auth.getIdentifier().getDomain() + " with challenge " + challenge.getDigest());
            DNSZoneRecord record = null;
            try
            {
                try (VirtDB db = VirtDB.connect())
                {
                    record = new DNSZoneRecord(zone, "TXT", "_acme-challenge." + DNSZone.qualifyZoneName(auth.getIdentifier().getDomain()), challenge.getDigest(), 10, 0, false, true);
                    db.setDNSZoneRecord(record);
                }
                logger.info("Triggering DNS challenge for " + auth.getIdentifier().getDomain());
                // trigger the challenge
                challenge.trigger();
                // poll the challenge
                for (int attempts = 0; challenge.getStatus() != Status.VALID && attempts < 30; attempts++)
                {
                    if (attempts > 5 && challenge.getStatus() == Status.INVALID)
                    {
                        logger.info("DNS challenge failed for " + auth.getIdentifier().getDomain() + " with " + challenge.getStatus() + " " + challenge.getError());
                        return false;
                    }
                    pollWait(1_500L + (attempts * 5_000L));
                    challenge.update();
                }
            }
            finally
            {
                // clean up
                if (record != null)
                {
                    try (VirtDB db = VirtDB.connect())
                    {
                        db.removeDNSZoneRecord(record.getId());
                    }
                }
            }
            logger.info("Zones DNS challenge complete " + challenge.getStatus());
            return challenge.getStatus() == Status.VALID;
        }
        return false;
    }
    
    private boolean authorizeViaHostedDNS(UUID accountId, Authorization auth) throws AcmeException
    {
        // Find the DNS challenge
        Dns01Challenge challenge = auth.findChallenge(Dns01Challenge.TYPE);
        if (challenge != null && challenge.getStatus() != Status.VALID)
        {
            logger.info("Setting up Hosted DNS challenge " + auth.getIdentifier().getDomain() + " with challenge " + challenge.getDigest());
            DNSRecord record = null;
            try
            {
                try (VirtDB db = VirtDB.connect())
                {
                    com.intrbiz.virt.model.Account account = db.getAccount(accountId);
                    String name = auth.getIdentifier().getDomain().substring(0, auth.getIdentifier().getDomain().length() - (account.getName().length() + this.hostedDomain.length() + 2));
                    if (name.length() > 0) name = "." + name;
                    record = new DNSRecord(account, DNSRecord.Scope.EXTERNAL, "TXT", "_acme-challenge" + name, challenge.getDigest(), 10, 0, false, true);
                    db.setDNSRecord(record);
                }
                logger.info("Triggering DNS challenge for " + auth.getIdentifier().getDomain());
                // trigger the challenge
                challenge.trigger();
                // poll the challenge
                for (int attempts = 0; challenge.getStatus() != Status.VALID && attempts < 30; attempts++)
                {
                    if (attempts > 5 && challenge.getStatus() == Status.INVALID)
                    {
                        logger.info("DNS challenge failed for " + auth.getIdentifier().getDomain() + " with " + challenge.getStatus() + " " + challenge.getError());
                        return false;
                    }
                    pollWait(1_500L + (attempts * 5_000L));
                    challenge.update();
                }
            }
            finally
            {
                // clean up
                if (record != null)
                {
                    try (VirtDB db = VirtDB.connect())
                    {
                        db.removeDNSRecord(record.getId());
                    }
                }
            }
            logger.info("Hosted DNS challenge complete " + challenge.getStatus());
            return challenge.getStatus() == Status.VALID;
        }
        return false;
    }
    
    private DNSZone findDNSZone(UUID accountId, Authorization auth)
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.findDNSZoneForNameInAccount(accountId, auth.getIdentifier().getDomain());
        }
    }
    
    private void pollWait(long time)
    {
        try
        {
            synchronized (this)
            {
                this.wait(time);
            }
        }
        catch (InterruptedException e)
        {
        }
    }
    
    private Session createAcmeSession() throws AcmeException
    {
        return new Session(this.acmeHost);
    }
    
    private ACMEAccount findAcmeAccount(VirtDB db, UUID currentAccountId)
    {
        // Get the ACME Account
        ACMEAccount acmeAccount = db.getACMEAccountsForAccount(currentAccountId);
        if (acmeAccount == null)
        {
            acmeAccount = new ACMEAccount(currentAccountId, KeyPairUtils.createKeyPair(KEY_PAIR_SIZE));
            db.setACMEAccount(acmeAccount);
            logger.info("Created new ACME account " + acmeAccount.getId() + " for account " + currentAccountId);
        }
        return acmeAccount;
    }
    
    private Account createAcmeAccount(ACMEAccount acmeAccount, Session session) throws AcmeException
    {
        Account account = new AccountBuilder()
                .agreeToTermsOfService()
                .useKeyPair(acmeAccount.loadKeyPair())
                .create(session);
        logger.info("Logged into ACME account: " + account.getLocation());
        return account;
    }
}
