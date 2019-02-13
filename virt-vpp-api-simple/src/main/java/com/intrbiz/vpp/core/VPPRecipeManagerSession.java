package com.intrbiz.vpp.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import com.intrbiz.vpp.api.VPPRecipeManager;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeReader;
import com.intrbiz.vpp.util.RecipeWriter;

public class VPPRecipeManagerSession implements VPPRecipeManager
{
    private static final Logger logger = Logger.getLogger(VPPRecipeManagerSession.class);
    
    private final VPPSimple session;
    
    private final File recipeStoreDir;
    
    private final RecipeReader reader = new RecipeReader();
    
    private final RecipeWriter writer = new RecipeWriter();
    
    private final Map<String, VPPRecipe> recipes = new TreeMap<String, VPPRecipe>();
    
    public VPPRecipeManagerSession(VPPSimple session, File recipeStoreDir) throws IOException, InterruptedException, ExecutionException
    {
        if (!(recipeStoreDir.exists() && recipeStoreDir.isDirectory())) throw new IllegalArgumentException("Invalid recipe store directory given: " + recipeStoreDir.getAbsolutePath());
        this.session = session;
        this.recipeStoreDir = recipeStoreDir;
        // Load any stored recipes and apply them
        this.loadRecipes();
        this.update();
    }
    
    @SuppressWarnings("unchecked")
    public void registerCustomRecipe(Class<? extends VPPRecipe>... types)
    {
        this.reader.registerSubType(types);
    }
    
    protected void deleteRecipe(String name)
    {
        new File(this.recipeStoreDir, name + ".yaml").delete();
    }
    
    protected void storeRecipe(VPPRecipe recipe)
    {
        this.writer.toFile(recipe, new File(this.recipeStoreDir, recipe.getName() + ".yaml"));
    }
    
    protected VPPRecipe loadRecipe(String name)
    {
        return this.reader.fromFile(VPPRecipe.class, new File(this.recipeStoreDir, name + ".yaml"));
    }
    
    protected void loadRecipes()
    {
        for (File file : this.recipeStoreDir.listFiles())
        {
            if (file.isFile() && file.getName().endsWith(".yaml"))
            {
                String name = file.getName().substring(0, file.getName().indexOf(".yaml"));
                VPPRecipe recipe = this.loadRecipe(name);
                if (recipe != null)
                {
                    this.recipes.put(name, recipe);
                    logger.info("Loaded recipe: " + name + " from " + file.getAbsolutePath());
                }
            }
        }
    }
    
    protected void applyRecipes() throws InterruptedException, ExecutionException
    {
        // TODO: Dependencies
        for (VPPRecipe recipe : this.recipes.values())
        {
            applyRecipe(recipe);
        }
    }
    
    protected void applyRecipe(VPPRecipe recipe) throws InterruptedException, ExecutionException
    {
        try
        {
            recipe.apply(this.session);
        }
        catch (InterruptedException | ExecutionException e)
        {
            logger.error("Failed to apply recipe: " + recipe.getName(), e);
            throw e;
        }
    }
    
    @Override
    public void update() throws InterruptedException, ExecutionException, IOException
    {
        this.applyRecipes();
    }
    
    @Override
    public <T extends VPPRecipe> T update(T recipe) throws InterruptedException, ExecutionException, IOException
    {
        Objects.requireNonNull(recipe);
        Objects.requireNonNull(recipe.getName());
        this.recipes.put(recipe.getName(), recipe);
        this.storeRecipe(recipe);
        // apply this recipe
        this.applyRecipe(recipe);
        return recipe;
    }
    
    @Override
    public Collection<? extends VPPRecipe> list()
    {
        return Collections.unmodifiableCollection(this.recipes.values());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends VPPRecipe> T get(String name)
    {
        Objects.requireNonNull(name);
        return (T) this.recipes.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends VPPRecipe> T remove(String name)
    {
        Objects.requireNonNull(name);
        VPPRecipe recipe = this.recipes.remove(name); 
        this.deleteRecipe(name);
        // TODO: unapply
        return (T) recipe;
    }

    public VPPSimple vpp()
    {
        return this.session;
    }
    
    public void close() throws IOException
    {
        this.session.close();
    }
}
