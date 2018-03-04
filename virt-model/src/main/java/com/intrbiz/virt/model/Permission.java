package com.intrbiz.virt.model;

public enum Permission
{
    GLOBAL_ADMIN,
    ACCOUNT_OWNER,
    NETWORK_MANAGE,
    IMAGE_MANAGE,
    MACHINE_MANAGE,
    STORAGE_MANAGE;
    
    public static final Permission fromString(String permission)
    {
        return Permission.valueOf(permission.toUpperCase());
    }
}