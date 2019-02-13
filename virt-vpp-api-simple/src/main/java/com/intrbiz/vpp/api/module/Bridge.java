package com.intrbiz.vpp.api.module;

import java.util.List;
import java.util.concurrent.Future;

import com.intrbiz.vpp.api.model.BridgeDomainDetail;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;

public interface Bridge
{
    Future<List<BridgeDomainDetail>> listBridgeDomains();
    
    Future<Void> createBridgeDomain(BridgeDomainId id, boolean learn, boolean forward, boolean uuFlood, boolean flood, boolean arpTerm, int macAge, Tag bdTag);
    
    Future<Void> removeBridgeDomain(BridgeDomainId id);
    
    Future<Void> addInterfaceToBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex, boolean bridgeVirtualInterface, SplitHorizonGroup splitHorizonGroup);
    
    default Future<Void> addInterfaceToBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex, SplitHorizonGroup splitHorizonGroup)
    {
        return this.addInterfaceToBridgeDomain(bridgeDomainId, interfaceIndex, false, splitHorizonGroup);
    }
    
    default Future<Void> addInterfaceToBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex)
    {
        return this.addInterfaceToBridgeDomain(bridgeDomainId, interfaceIndex, false, SplitHorizonGroup.DEFAULT);
    }
    
    Future<Void> removeInterfaceFromBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex, boolean bridgeVirtualInterface, SplitHorizonGroup splitHorizonGroup);
    
    default Future<Void> removeInterfaceFromBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex, SplitHorizonGroup splitHorizonGroup)
    {
        return this.removeInterfaceFromBridgeDomain(bridgeDomainId, interfaceIndex, false, splitHorizonGroup);
    }
    
    default Future<Void> removeInterfaceFromBridgeDomain(BridgeDomainId bridgeDomainId, InterfaceIndex interfaceIndex)
    {
        return this.removeInterfaceFromBridgeDomain(bridgeDomainId, interfaceIndex, false, SplitHorizonGroup.DEFAULT);
    }
}
