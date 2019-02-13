package com.intrbiz.vpp.api.recipe;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.vpp.api.model.InterfaceIndex;

/**
 * A smart recipe which will create some kind of VPP interface
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
public interface VPPInterfaceRecipe
{
    /**
     * The current (transient) index of the created interface, 
     * this should only be accessed after apply has been executed.
     * @return the current index of the created interface
     */
    InterfaceIndex getCurrentInterfaceIndex();
}
