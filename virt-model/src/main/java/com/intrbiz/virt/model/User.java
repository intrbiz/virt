package com.intrbiz.virt.model;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@SQLTable(schema = VirtDB.class, name = "user", since = @SQLVersion({ 1, 0, 0 }))
public class User implements Principal
{
    public static final int BCRYPT_WORK_FACTOR = 12;
    
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @SQLColumn(index = 2, name = "email", since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "email_unq")
    private String email;

    @SQLColumn(index = 3, name = "full_name", since = @SQLVersion({ 1, 0, 0 }))
    private String fullName;

    @SQLColumn(index = 4, name = "given_name", since = @SQLVersion({ 1, 0, 0 }))
    private String givenName;

    @SQLColumn(index = 5, name = "mobile", since = @SQLVersion({ 1, 0, 0 }))
    private String mobile;

    @SQLColumn(index = 6, name = "password", since = @SQLVersion({ 1, 0, 0 }))
    private String password;

    @SQLColumn(index = 7, name = "locked", since = @SQLVersion({ 1, 0, 0 }))
    private boolean locked;

    @SQLColumn(index = 8, name = "password_change", since = @SQLVersion({ 1, 0, 0 }))
    private boolean passwordChange;

    @SQLColumn(index = 9, name = "created", since = @SQLVersion({ 1, 0, 0 }))
    private Timestamp created;
    
    @SQLColumn(index = 10, name = "superuser", since = @SQLVersion({ 1, 0, 1 }))
    private boolean superuser = false;
    
    private transient List<UserAccountGrant> accountGrants;
    
    private transient List<Account> accounts;

    public User()
    {
        super();
    }

    public User(String email, String fullName, String givenName, String mobile)
    {
        super();
        this.id = UUID.randomUUID();
        this.email = email;
        this.fullName = fullName;
        this.givenName = givenName;
        this.mobile = mobile;
        this.locked = false;
        this.passwordChange = true;
        this.created = new Timestamp(System.currentTimeMillis());
        this.superuser = false;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void hashPassword(String plainPassword)
    {
        this.password = BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
        // reset as we've updated the password
        this.passwordChange = false;
    }

    public boolean verifyPassword(String plainPassword)
    {
        if (plainPassword == null || this.password == null) return false;
        return BCrypt.checkpw(plainPassword, this.password);
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public boolean isPasswordChange()
    {
        return passwordChange;
    }

    public void setPasswordChange(boolean passwordChange)
    {
        this.passwordChange = passwordChange;
    }

    public Timestamp getCreated()
    {
        return created;
    }

    public void setCreated(Timestamp created)
    {
        this.created = created;
    }
    
    public boolean isSuperuser()
    {
        return superuser;
    }

    public void setSuperuser(boolean superuser)
    {
        this.superuser = superuser;
    }

    @Override
    public String getName()
    {
        return "User[" + id + "::" + email + "]";
    }

    public List<UserAccountGrant> getAccountGrants()
    {
        if (this.accountGrants == null)
        {
            try (VirtDB db = VirtDB.connect())
            {
                this.accountGrants = db.getUserAccountGrantsForUser(this.id);
            }
        }
        return this.accountGrants;
    }
    
    public List<Account> getAccounts()
    {
        if (this.accounts == null)
        {
            try (VirtDB db = VirtDB.connect())
            {
                this.accounts = db.getAccountsForUser(this.id);
            }
        }
        return this.accounts;
    }
    
    public List<Account> getActiveAccounts()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getActiveAccountsForUser(this.id);
        }
    }
    
    public List<Account> getOwnedAccounts()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getAccountsOwnedByUser(this.id);
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        User other = (User) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "User[id=" + id + ", email=" + email + "]";
    }
}
