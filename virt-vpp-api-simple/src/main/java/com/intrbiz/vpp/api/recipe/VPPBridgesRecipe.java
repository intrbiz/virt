package com.intrbiz.vpp.api.recipe;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.vpp.api.model.BridgeDomainId;

/**
 * A smart recipe which will create a set of VPP bridge domains
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
public interface VPPBridgesRecipe
{
    Collection<? extends VPPBridgeRecipe> getBridges();
    
    <T extends VPPBridgeRecipe> T getBridge(BridgeDomainId id);
}
