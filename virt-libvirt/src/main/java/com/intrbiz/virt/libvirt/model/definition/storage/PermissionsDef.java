package com.intrbiz.virt.libvirt.model.definition.storage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="permissions")
@XmlType(name="permissions")
public class PermissionsDef
{
    private String mode;
    
    private String owner;
    
    private String group;

    @XmlElement(name="mode")
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XmlElement(name="owner")
    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    @XmlElement(name="group")
    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }
}
