package com.intrbiz.system.net;

import static com.intrbiz.system.exec.Command.*;

import java.util.List;

import com.intrbiz.system.exec.SystemExecutionException;
import com.intrbiz.system.exec.SystemExecutorService;
import com.intrbiz.system.sysfs.SysFs;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;

public class LinuxNetManager implements NetManager
{
    private static final int INTERFACE_NAME_SIZE = 15;
    
    private static final String IP = "/sbin/ip";
    
    private static final String LINK = "link";
    
    private static final String ADD = "add";
    
    private static final String DEL = "del";
    
    private static final String DEV = "dev";
    
    private static final String TYPE = "type";
    
    private static final String VETH = "veth";
    
    private static final String NAME = "name";
    
    private static final String PEER = "peer";
    
    private static final String UP = "up";
    
    private static final String DOWN = "down";
    
    private static final String SET = "set";
    
    private static final String PROMISC = "promisc";
    
    private static final String ON = "on";
    
    private static final String OFF = "off";
    
    private static final String MTU = "mtu";
    
    private static final String ADDRESS = "address";

    private final SystemExecutorService executor = SystemExecutorService.getSystemExecutorService();
    
    private final SysFs sysFs = SysFs.sysFs();

    public LinuxNetManager()
    {
        super();
    }

    @Override
    public List<String> getInterfaces()
    {
        return this.sysFs.getInterfaces();
    }

    @Override
    public MACAddress getInterfaceMAC(String name) throws NetException
    {
        return this.sysFs.getInterfaceMAC(name);
    }

    @Override
    public MTU getInterfaceMTU(String name)
    {
        return this.sysFs.getInterfaceMTU(name);
    }

    @Override
    public void createVeth(String name, String peerName) throws NetException
    {
        try
        {
            // ip link add <p1-name> type veth peer name <p2-name>
            name = truncateInterfaceName(name);
            peerName = truncateInterfaceName(peerName);
            this.executor.expect(command(IP, LINK, ADD, name, TYPE, VETH, PEER, NAME, peerName), 0, 2);
        }
        catch (SystemExecutionException e) 
        {
            throw new NetException("Failed to create veth interface '" + name + "'", e);
        }
    }

    @Override
    public void destroyVeth(String name) throws NetException
    {
        try
        {
            // ip link del dev <p1-name>
            name = truncateInterfaceName(name);
            this.executor.expect(command(IP, LINK, DEL, DEV, name), 0, 1);
        }
        catch (SystemExecutionException e) 
        {
            throw new NetException("Failed to destroy veth interface '" + name + "'", e);
        }
    }

    @Override
    public void setUp(String name) throws NetException
    {
        try
        {
            // ip link set dev <name> up
            name = truncateInterfaceName(name);
            this.executor.expect(command(IP, LINK, SET, DEV, name, UP), 0);
        }
        catch (SystemExecutionException e) 
        {
            throw new NetException("Failed to set interface '" + name + "' up", e);
        }
    }

    @Override
    public void setDown(String name) throws NetException
    {
        try
        {
            // ip link set dev <name> up
            name = truncateInterfaceName(name);
            this.executor.expect(command(IP, LINK, SET, DEV, name, DOWN), 0);
        }
        catch (SystemExecutionException e) 
        {
            throw new NetException("Failed to set interface '" + name + "' down", e);
        }
    }

    @Override
    public void promiscuousMode(String name, boolean on) throws NetException
    {
        try
        {
            // ip link set dev <name> promisc on, off
            name = truncateInterfaceName(name);
            this.executor.expect(command(IP, LINK, SET, DEV, name, PROMISC, on ? ON : OFF), 0);
        }
        catch (SystemExecutionException e) 
        {
            throw new NetException("Failed to set interface '" + name + "' promisc " + on, e);
        }
    }

    @Override
    public void setMTU(String name, MTU mtu) throws NetException
    {
        try
        {
            // ip link set dev <name> mtu <mtu>
            name = truncateInterfaceName(name);
            this.executor.expect(command(IP, LINK, SET, DEV, name, MTU, mtu.getValueAsString()), 0);
        }
        catch (SystemExecutionException e) 
        {
            throw new NetException("Failed to set interface '" + name + "' mtu " + mtu, e);
        }
    }

    @Override
    public void setMAC(String name, MACAddress mac) throws NetException
    {
        try
        {
            // ip link set dev <name> address <mac>
            name = truncateInterfaceName(name);
            this.executor.expect(command(IP, LINK, SET, DEV, name, ADDRESS, mac.toString()), 0);
        }
        catch (SystemExecutionException e) 
        {
            throw new NetException("Failed to set interface '" + name + "' mac address " + mac, e);
        }
    }
    
    private static String truncateInterfaceName(String name)
    {
        return name.length() > INTERFACE_NAME_SIZE ? name.substring(0, INTERFACE_NAME_SIZE) : name;
    }
}
