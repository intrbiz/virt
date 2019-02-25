package com.intrbiz.vpp.api.recipe;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.vpp.api.VPPSimple;

/**
 * A smart recipe which can create useful thing using VPP.
 * 
 * Recipes are intended to be idempotent ways of modelling what you want VPP to achieve, 
 * the recipe will then take care of ensuring VPP is in the desired state.
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
public interface VPPRecipe
{
    String getType();
    
    String getName();

    void setName(String name);

    Set<String> getDepends();

    void setDepends(Set<String> depends);
    
    void addDepends(String depend);
    
    void addDepends(VPPRecipe depend);
    
    int getDependCount();

    /**
     * Apply this recipe to the given VPP session in an idempotent fashion.
     * 
     * This should make any changes to VPP as needed.  If a change as already been made, 
     * this should be ignored and skipped over.
     * 
     * If this recipe cannot apply the changes it needs to a <code>ExecutionException</code> should be thrown.
     * 
     * @param session the VPP session to configure
     * @throws InterruptedException
     * @throws ExecutionException
     */
    void apply(VPPSimple session, VPPRecipeContext context) throws InterruptedException, ExecutionException;
    
    /**
     * Unapply this recipe from the given VPP session in an idempotent fashion.
     * 
     * This should teardown any changes this recipe had made, if something has 
     * already been torndown that should be ignored.
     * 
     * If this recipe cannot currently be torndown an <code>ExecutionException</code> should be thrown.
     * 
     * @param session the VPP session to configure
     * @throws InterruptedException
     * @throws ExecutionException
     */
    void unapply(VPPSimple session, VPPRecipeContext context) throws InterruptedException, ExecutionException;
}
