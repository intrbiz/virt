package com.intrbiz.vpp.api.recipe;

import java.util.concurrent.ExecutionException;

public interface VPPRecipeContext
{

    /**
     * Does the dependency of the given name exist
     * @param name
     * @return true if the dependency exists
     */
    boolean hasDependency(String name);
    
    /**
     * Get the dependency of the given name.  Throwing an exception if it does not.
     * 
     * @param name
     * @return the dependency
     * @throws ExecutionException if the given dependency does not exist
     */
    <T extends VPPRecipe> T getDependency(String name) throws ExecutionException;
    
}
