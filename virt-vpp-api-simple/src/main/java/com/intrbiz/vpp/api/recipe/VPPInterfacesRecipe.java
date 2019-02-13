package com.intrbiz.vpp.api.recipe;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * A smart recipe which will create a set of VPP interfaces
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
public interface VPPInterfacesRecipe
{
    Collection<? extends VPPInterfaceRecipe> getInterfaces();
}
