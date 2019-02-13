package com.intrbiz.vpp.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.core.VPPRecipeManagerSession;

public interface VPPRecipeManager extends AutoCloseable
{
    @SuppressWarnings("unchecked")
    void registerCustomRecipe(Class<? extends VPPRecipe>... types);
    
    /**
     * Get access to the underlying VPP session
     * @return
     */
    VPPSimple vpp();
    
    // management
    
    /**
     * Register the given recipe under the given name.  If a recipe already exists with the given name, it will be 
     * updated to the given state.  The recipe is also persistently stored, and will be reloaded if this daemon is 
     * restarted.
     * 
     * The updated recipe will be immediately applied to VPP.
     * 
     * @param name the recipe name
     * @param recipe the recipe
     * @return the recipe
     */
    <T extends VPPRecipe> T update(T recipe) throws InterruptedException, ExecutionException, IOException;
    
    /**
     * Update VPP to reflect the recipes state, should be called periodically to ensure VPP is correct
     */
    void update() throws InterruptedException, ExecutionException, IOException;
    
    /**
     * List all the registered recipes
     */
    Collection<? extends VPPRecipe> list();
    
    /**
     * Get the recipe currently registered with the given name
     * @param name the recipe name
     * @return the recipe which is registered or null
     */
    <T extends VPPRecipe> T get(String name);
    
    /**
     * Remove the recipe which is registered with the given name and remove any stored state.
     * 
     * @param name the recipe name
     * @return the removed recipe or null
     */
    <T extends VPPRecipe> T remove(String name) throws InterruptedException, ExecutionException, IOException;
    
    /**
     * Close this session to the VPP daemon.
     * 
     * Note: this will not remove any recipes nor will it deleted the saved recipe state.
     */
    void close() throws IOException;
    
    
    public static VPPRecipeManager connect(String clientName, File recipeStoreDir) throws IOException, InterruptedException, ExecutionException
    {
        return wrap(VPPSimple.connect(clientName), recipeStoreDir);
    }
    
    public static VPPRecipeManager wrap(VPPSimple session, File recipeStoreDir) throws IOException, InterruptedException, ExecutionException
    {
        return new VPPRecipeManagerSession(session, recipeStoreDir);
    }
}
