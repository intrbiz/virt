package com.intrbiz.virt.model;

import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.util.CSRBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.data.db.compiler.meta.Action;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLForeignKey;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.util.ACMEUtil;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("acme_certificate")
@SQLTable(schema = VirtDB.class, name = "acme_certificate", since = @SQLVersion({ 1, 0, 17 }))
public class ACMECertificate
{    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 17 }))
    @SQLPrimaryKey()
    private UUID id;
    
    @JsonProperty("account_id")
    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 17 }))
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 17 }))
    private UUID accountId;
    
    @JsonIgnore
    @SQLColumn(index = 3, name = "acme_account_id", notNull = true, since = @SQLVersion({ 1, 0, 17 }))
    @SQLForeignKey(references = ACMEAccount.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 17 }))
    private UUID acmeAccountId;
    
    @JsonProperty("created")
    @SQLColumn(index = 4, name = "created", since = @SQLVersion({ 1, 0, 17 }))
    private Timestamp created;
    
    @JsonProperty("key_pair")
    @SQLColumn(index = 5, name = "key_pair", notNull = true, since = @SQLVersion({ 1, 0, 17 }))
    private String keyPair;
    
    @JsonProperty("domains")
    @SQLColumn(index = 6, name = "domains", type="TEXT[]", since = @SQLVersion({ 1, 0, 17 }))
    private List<String> domains = new LinkedList<String>();
    
    @JsonProperty("request")
    @SQLColumn(index = 7, name = "request", since = @SQLVersion({ 1, 0, 17 }))
    private String request;
    
    @JsonProperty("certificate")
    @SQLColumn(index = 8, name = "certificate", since = @SQLVersion({ 1, 0, 17 }))
    private String certificate;
    
    @JsonProperty("certificate_bundle")
    @SQLColumn(index = 9, name = "certificate_bundle", since = @SQLVersion({ 1, 0, 17 }))
    private String certificateBundle;
    
    @JsonProperty("issued_at")
    @SQLColumn(index = 10, name = "issued_at", since = @SQLVersion({ 1, 0, 17 }))
    private Timestamp issuedAt;
    
    @JsonProperty("expires_at")
    @SQLColumn(index = 11, name = "expires_at", since = @SQLVersion({ 1, 0, 17 }))
    private Timestamp expiresAt;
    
    @JsonProperty("location")
    @SQLColumn(index = 12, name = "location", since = @SQLVersion({ 1, 0, 17 }))
    private String location;
    
    @JsonProperty("generated")
    @SQLColumn(index = 9, name = "generated", since = @SQLVersion({ 1, 0, 23 }))
    private boolean generated;

    public ACMECertificate()
    {
        super();
    }
    
    public ACMECertificate(UUID accountId, ACMEAccount acmeAccount, KeyPair keyPair, List<String> domains, boolean generated)
    {
        super();
        this.id = Account.randomId(accountId);
        this.accountId = accountId;
        this.acmeAccountId = acmeAccount.getId();
        this.created = new Timestamp(System.currentTimeMillis());
        this.keyPair = ACMEUtil.keyPairToString(keyPair);
        this.domains = domains;
        this.generated = generated;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public Timestamp getCreated()
    {
        return created;
    }

    public void setCreated(Timestamp created)
    {
        this.created = created;
    }

    public String getKeyPair()
    {
        return keyPair;
    }

    public void setKeyPair(String keyPair)
    {
        this.keyPair = keyPair;
    }
    
    public KeyPair loadKeyPair()
    {
        return ACMEUtil.keyPairFromString(this.keyPair);
    }

    public UUID getAcmeAccountId()
    {
        return acmeAccountId;
    }

    public void setAcmeAccountId(UUID acmeAccountId)
    {
        this.acmeAccountId = acmeAccountId;
    }

    public List<String> getDomains()
    {
        return domains;
    }

    public void setDomains(List<String> domains)
    {
        this.domains = domains;
    }
    
    @JsonIgnore()
    public String getDomainsSummary()
    {
        return this.domains.stream().collect(Collectors.joining(", "));
    }
    
    public String getRequest()
    {
        return request;
    }

    public void setRequest(String request)
    {
        this.request = request;
    }
    
    public void saveRequest(CSRBuilder csr)
    {
        this.request = ACMEUtil.csrToString(csr);
    }

    public String getCertificate()
    {
        return certificate;
    }

    public void setCertificate(String certificate)
    {
        this.certificate = certificate;
    }

    public String getCertificateBundle()
    {
        return certificateBundle;
    }

    public void setCertificateBundle(String certificateBundle)
    {
        this.certificateBundle = certificateBundle;
    }

    public Timestamp getIssuedAt()
    {
        return issuedAt;
    }

    public void setIssuedAt(Timestamp issuedAt)
    {
        this.issuedAt = issuedAt;
    }

    public Timestamp getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt)
    {
        this.expiresAt = expiresAt;
    }
    
    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public boolean isGenerated()
    {
        return generated;
    }

    public void setGenerated(boolean generated)
    {
        this.generated = generated;
    }
    
    public void issued(Certificate certificate)
    {
        this.certificate = ACMEUtil.certToString(certificate);
        this.certificateBundle = ACMEUtil.certToBundleString(certificate);
        this.issuedAt = new Timestamp(System.currentTimeMillis());
        this.expiresAt = new Timestamp(certificate.getCertificate().getNotAfter().getTime());
        this.location = certificate.getLocation().toString();
    }

    @JsonIgnore()
    public boolean isPending()
    {
        return this.issuedAt == null;
    }
    
    @JsonIgnore()
    public boolean isIssued()
    {
        return this.issuedAt != null && this.expiresAt != null && this.expiresAt.after(new Timestamp(System.currentTimeMillis()));
    }
    
    @JsonIgnore()
    public boolean isExpired()
    {
        return this.issuedAt != null && this.expiresAt != null && this.expiresAt.before(new Timestamp(System.currentTimeMillis()));
    }

    @JsonIgnore()
    public ACMEAccount getAcmeAccount()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getACMEAccount(this.acmeAccountId);
        }
    }
    
    @JsonIgnore()
    public Account getAccount()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getAccount(this.accountId);
        }
    }
}
