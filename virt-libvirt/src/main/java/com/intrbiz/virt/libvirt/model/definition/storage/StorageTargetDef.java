package com.intrbiz.virt.libvirt.model.definition.storage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="target")
@XmlType(name="target")
public class StorageTargetDef
{
    private String path;
    
    private StorageFormatDef format;
    
    private PermissionsDef permissionsDef;

    @XmlElement(name="path")
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @XmlElementRef(type=StorageFormatDef.class)
    public StorageFormatDef getFormat()
    {
        return format;
    }

    public void setFormat(StorageFormatDef format)
    {
        this.format = format;
    }

    @XmlElementRef(type=PermissionsDef.class)
    public PermissionsDef getPermissions()
    {
        return permissionsDef;
    }

    public void setPermissions(PermissionsDef permissionsDef)
    {
        this.permissionsDef = permissionsDef;
    }
}
