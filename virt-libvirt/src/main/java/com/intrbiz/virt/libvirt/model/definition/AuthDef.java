package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "auth")
@XmlType(name = "auth")
public class AuthDef
{
    private String username;
    
    private SecretDef secret;

    public AuthDef()
    {
        super();
    }
    
    public AuthDef(String username, SecretDef secret)
    {
        super();
        this.username = username;
        this.secret = secret;
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

    @XmlElementRef(type = SecretDef.class)
    public SecretDef getSecret()
    {
        return secret;
    }

    public void setSecret(SecretDef secret)
    {
        this.secret = secret;
    }
}
