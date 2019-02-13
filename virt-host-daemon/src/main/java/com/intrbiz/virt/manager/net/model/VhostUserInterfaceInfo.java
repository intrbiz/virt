package com.intrbiz.virt.manager.net.model;

/**
 * A guest NIC which uses vhost-user 
 */
public class VhostUserInterfaceInfo extends InterfaceInfo
{
    private final String path;
    
    private final boolean server;

    public VhostUserInterfaceInfo(String mac, String path, boolean server)
    {
        super(mac);
        this.path = path;
        this.server = server;
    }

    public String getPath()
    {
        return path;
    }

    public boolean isServer()
    {
        return server;
    }

    @Override
    public String toString()
    {
        return "VhostUserNICInfo [path=" + path + ", server=" + server + ", mac=" + getMac() + "]";
    }
}
