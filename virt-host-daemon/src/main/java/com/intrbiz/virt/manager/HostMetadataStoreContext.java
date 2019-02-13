package com.intrbiz.virt.manager;

public interface HostMetadataStoreContext
{
    <T> T get(String key);
    
    void set(String key, Object value);
}
