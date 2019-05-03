package com.intrbiz.virt.model;

public interface DNSContent
{
    String getType();

    String getContent();
    
    int getTtl();
    
    int getPriority();
    
    String getZoneName(String hostedDomainName);
}
