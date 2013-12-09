package com.intrbiz.virt.dash.cfg;

import java.security.Principal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.mindrot.jbcrypt.BCrypt;

@XmlRootElement(name = "user")
@XmlType(name = "user")
public class VirtDashUser implements Principal
{
    public static final int BCRYPT_WORK_FACTOR = 12;

    private String username;

    private String fullName;

    private String passwordHash;

    public VirtDashUser()
    {
        super();
    }

    public VirtDashUser(String username, String fullName)
    {
        super();
        this.username = username;
        this.fullName = fullName;
    }

    @XmlAttribute(name = "username")
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @XmlAttribute(name = "name")
    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    @XmlAttribute(name = "password")
    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public void setPassword(String plainPassword)
    {
        this.hashPassword(plainPassword);
    }

    public void hashPassword(String plainPassword)
    {
        this.passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    public boolean verifyPassword(String plainPassword)
    {
        return BCrypt.checkpw(plainPassword, this.passwordHash);
    }

    @Override
    public String getName()
    {
        return this.username;
    }
}
