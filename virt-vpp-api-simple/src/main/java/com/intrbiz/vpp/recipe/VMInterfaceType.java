package com.intrbiz.vpp.recipe;

import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.MACAddress;

public enum VMInterfaceType {
    VETH,
    HOST,
    VHOST_USER;
    
    public VMInterface createVMInterface(MACAddress vmMACAddress, BridgeDomainId bridgeId)
    {
        switch (this) {
            case HOST:
                return VMInterface.forVMUsingHostInterface(vmMACAddress, bridgeId);
            case VETH:
                return VMInterface.forVMUsingVethHostInterface(vmMACAddress, bridgeId);
            case VHOST_USER:
                return VMInterface.forVMUsingVhostUserInterface(vmMACAddress, bridgeId);
        }
        return null;
    }
}