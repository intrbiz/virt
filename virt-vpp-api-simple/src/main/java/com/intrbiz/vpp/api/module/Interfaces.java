package com.intrbiz.vpp.api.module;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.model.VRFIndex;
import com.intrbiz.vpp.api.model.VhostUserMode;

/**
 * VPP Module to manage interfaces
 */
public interface Interfaces
{
    /**
     * List the interfaces in this VPP instance
     */
    Future<List<InterfaceDetail>> listInterfaces();
    
    Future<InterfaceDetail> getInterface(InterfaceIndex index);
    
    Future<InterfaceDetail> getInterface(String name);
    
    default Future<InterfaceDetail> getHostInterface(String name)
    {
        return getInterface("host-" + name);
    }
    
    /**
     * Create a host interface in VPP.
     * 
     * This create a Linux AF Packet interface bound to the given Linux kernel interface within VPP.
     * 
     * This allows VPP to send and receive data on a Linux kernel interface
     * 
     * @param hostInterfaceName the name of the corresponding Linux interface to which the VPP interface is bound
     * 
     */
    Future<InterfaceIndex> createHostInterface(String hostInterfaceName, MACAddress mac);
    
    default Future<InterfaceIndex> createHostInterface(String hostInterfaceName)
    {
        return this.createHostInterface(hostInterfaceName, null);
    }
    
    Future<Void> removeHostInterface(String hostInterfaceName);
    
    /**
     * Add the given IPv4 address to the given interface
     * @param index the index of the interface
     * @param address the IPv4 address
     * @return nothing
     */
    Future<Void> addInterfaceIPv4Address(InterfaceIndex index, IPv4CIDR address);
    
    /**
     * Remove the given IPv4 address from the given interface
     * @param index the index of the interface
     * @param address the IPv4 address
     * @return nothing
     */
    Future<Void> removeInterfaceIPv4Address(InterfaceIndex index, IPv4CIDR address);
    
    /**
     * Set the user defined 'tag' for the given interface.  
     * @param index the index of the interface
     * @param tag the tag (upto 64 bytes long) or null to clear
     */
    Future<Void> setInterfaceTag(InterfaceIndex index, Tag tag);
    
    /**
     * Set the IP routing table for the given interface to the given IPv4 VRF
     * @param index the index of the interface
     * @param vrf the index of the VRF table
     */
    Future<Void> setInterfaceIPv4VRF(InterfaceIndex index, VRFIndex vrf);
    
    /**
     * Remove ALL IPv4 addresses from the given interface
     * @param index the index of the interface
     * @return nothing
     */
    Future<Void> removeInterfaceIPv4Addresses(InterfaceIndex index);
    
    /**
     * Set the admin state of the given interface to up
     * @param index the index of the interface
     * @return nothing
     */
    Future<Void> setInterfaceUp(InterfaceIndex index);
    
    /**
     * Set the admin state of the given interface to down
     * @param index the index of the interface
     * @return nothing
     */
    Future<Void> setInterfaceDown(InterfaceIndex index);
    
    /**
     * Set the MTU for the given interface
     * @param index the index of the interface
     * @param mtu the maximum transmit unit in bytes
     * @return nothing
     */
    Future<Void> setInterfaceMTU(InterfaceIndex index, MTU mtu);
    
    /**
     * Set the MAC address for the given interface
     * @param index the index of the interface
     * @param macAddress the MAC address for the intrface
     * @return nothing
     */
    Future<Void> setInterfaceMACAddress(InterfaceIndex index, MACAddress macAddress);
    
    /**
     * Create a loopback interface, eg: for bridge virtual interfaces
     * @param macAddress the MAC address for the loopback interface
     * @return the created interface index
     */
    Future<InterfaceIndex> createLoopbackInterface(MACAddress macAddress);
    
    /**
     * Remove the given loopback interface
     * @param interfaceIndex the index of the interface
     * @return nothing
     */
    Future<Void> removeLoopbackInterface(InterfaceIndex interfaceIndex);
    
    /**
     * Create a vhost-user interface, a high performance userspace network interface using the 
     * VirtIO transport protocol.  This uses a Unix socket for the control plane and shared memory 
     * for the data plane.  This is primarily used to interconnect VPP with VMs.  
     * @param controlSocketFile
     * @param server
     * @param macAddress
     * @return
     */
    Future<InterfaceIndex> createVhostUserInterface(Path controlSocketFile, VhostUserMode mode, MACAddress macAddress);
    
    default Future<InterfaceIndex> createVhostUserInterface(Path controlSocketFile, VhostUserMode mode)
    {
        return this.createVhostUserInterface(controlSocketFile, mode, null);
    }
    
    default Future<InterfaceIndex> createVhostUserInterface(Path controlSocketFile)
    {
        return this.createVhostUserInterface(controlSocketFile, VhostUserMode.CLIENT, null);
    }
    
    Future<Void> removeVhostUserInterface(InterfaceIndex interfaceIndex);
}
