package com.intrbiz.virt.model;

import java.util.EnumSet;

public enum Role
{
    ACCOUNT_OWNER(EnumSet.of(Permission.ACCOUNT_OWNER, Permission.NETWORK_MANAGE, Permission.IMAGE_MANAGE, Permission.MACHINE_MANAGE, Permission.STORAGE_MANAGE)),
    ACCOUNT_ADMIN(EnumSet.of(Permission.ACCOUNT_OWNER, Permission.NETWORK_MANAGE, Permission.IMAGE_MANAGE, Permission.MACHINE_MANAGE, Permission.STORAGE_MANAGE)),
    MACHINE_ADMIN(EnumSet.of(Permission.MACHINE_MANAGE, Permission.STORAGE_MANAGE)),
    READ_ONLY(EnumSet.noneOf(Permission.class));
    
    private final EnumSet<Permission> permissions;
    
    private Role(EnumSet<Permission> permissions)
    {
        this.permissions = permissions;
    }
    
    public EnumSet<Permission> getPermissions()
    {
        return this.permissions;
    }
    
    public boolean hasPermission(Permission permission)
    {
        return this.permissions.contains(permission);
    }
}