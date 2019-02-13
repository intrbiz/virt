package com.intrbiz.virt.manager.net;

import java.util.Set;

import com.intrbiz.virt.config.NetManagerCfg;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineInterfaceEO;
import com.intrbiz.virt.event.model.NetworkEO;
import com.intrbiz.virt.manager.Manager;
import com.intrbiz.virt.manager.net.model.InterfaceInfo;

public interface NetManager extends Manager<NetManagerCfg>
{   
    /**
     * Get the address this host uses to interconnect with other hosts
     */
    String getInterconnectAddress();
    
    /**
     * Register a remote VM Host to interconnect with
     */
    void registerRemoteVMHost(String remoteVMHostAddress);
    
    default void registerRemoteVMHosts(Set<String> remoteVMHostAddresses)
    {
        for (String remoteVMHostAddress : remoteVMHostAddresses)
        {
            this.registerRemoteVMHost(remoteVMHostAddress);
        }
    }
    
    /**
     * Get the supported network types
     */
    Set<String> getSupportedNetworkTypes();
    
    /**
     * Is the give network type supported
     */
    boolean isSupported(NetworkEO net);
    
    /**
     * Create the given network on this host
     */
    void setupNetwork(NetworkEO net);
    
    /**
     * Create the given guest metadata NIC on this host 
     */
    InterfaceInfo setupGuestMetadataNIC(MachineEO machine);
    
    /**
     * Create the given guest NIC on this host
     */
    InterfaceInfo setupGuestNIC(MachineInterfaceEO nic);
    
    /**
     * Start the given guest metadata NIC on this host 
     */
    void startGuestMetadataNIC(MachineEO machine);
    
    /**
     * Start the given guest NIC on this host
     */
    void startGuestNIC(MachineInterfaceEO nic);
    
    /**
     * Start the given guest metadata NIC on this host 
     */
    void stopGuestMetadataNIC(MachineEO machine);
    
    /**
     * Start the given guest NIC on this host
     */
    void stopGuestNIC(MachineInterfaceEO nic);
    
    /**
     * Start the given guest metadata NIC on this host 
     */
    void removeGuestMetadataNIC(MachineEO machine);
    
    /**
     * Start the given guest NIC on this host
     */
    void removeGuestNIC(MachineInterfaceEO nic);
}
