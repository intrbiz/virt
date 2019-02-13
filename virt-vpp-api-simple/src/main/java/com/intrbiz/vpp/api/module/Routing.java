package com.intrbiz.vpp.api.module;

import java.util.concurrent.Future;

import com.intrbiz.vpp.api.model.VRFIndex;

public interface Routing
{
    Future<Void> createIPv4VRF(VRFIndex index, String name);
    
    Future<Void> removeIPv4VRF(VRFIndex index);
}
