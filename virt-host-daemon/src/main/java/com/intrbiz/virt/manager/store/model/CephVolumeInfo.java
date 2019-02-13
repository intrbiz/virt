package com.intrbiz.virt.manager.store.model;

public class CephVolumeInfo extends VolumeInfo
{
    private final String hosts;
    
    private final String auth;
    
    private final String source;
    
    public CephVolumeInfo(long size, String hosts, String auth, String source)
    {
        super(size);
        this.hosts = hosts;
        this.auth = auth;
        this.source = source;
    }

    public String getHosts()
    {
        return hosts;
    }

    public String getAuth()
    {
        return auth;
    }

    public String getSource()
    {
        return source;
    }

    @Override
    public String toString()
    {
        return "CephVolumeInfo [hosts=" + hosts + ", auth=" + auth + ", source=" + source + ", size=" + getSize() + "]";
    }
}
