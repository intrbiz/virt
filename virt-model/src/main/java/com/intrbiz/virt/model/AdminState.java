package com.intrbiz.virt.model;

public enum AdminState
{
    DISABLED("Disabled"),
    IN_MAINTENANCE("In Maintenance"),
    BACKUP("Backup"),
    ENABLED("Enabled");
    
    private final String summary;
    
    private AdminState(String summary)
    {
        this.summary = summary;
    }
    
    public String getSummary()
    {
        return this.summary;
    }
}