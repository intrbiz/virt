package com.intrbiz.vpp.api.recipe;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.vpp.api.model.BridgeDomainId;

/**
 * A smart recipe which will create a VPP bridge domain
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
public interface VPPBridgeRecipe
{

    BridgeDomainId getId();
}
