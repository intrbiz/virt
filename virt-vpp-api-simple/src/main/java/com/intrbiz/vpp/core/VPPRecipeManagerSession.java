package com.intrbiz.vpp.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.vpp.api.VPPRecipeManager;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipeContext;
import com.intrbiz.vpp.util.RecipeReader;
import com.intrbiz.vpp.util.RecipeWriter;

public class VPPRecipeManagerSession implements VPPRecipeManager
{
    public static final long UPDATE_PERIOD = 60_000L;
    
    private static final Logger logger = Logger.getLogger(VPPRecipeManagerSession.class);
    
    private final VPPSimple session;
    
    private final File recipeStoreDir;
    
    private final RecipeReader reader = RecipeReader.getDefault();
    
    private final RecipeWriter writer = RecipeWriter.getDefault();
    
    private final Map<String, VPPRecipe> recipes = new TreeMap<String, VPPRecipe>();
    
    private final Timer updateTimer = new Timer();
    
    private final TimerTask updateTask;
    
    public VPPRecipeManagerSession(VPPSimple session, File recipeStoreDir) throws IOException, InterruptedException, ExecutionException
    {
        if (!(recipeStoreDir.exists() && recipeStoreDir.isDirectory())) throw new IllegalArgumentException("Invalid recipe store directory given: " + recipeStoreDir.getAbsolutePath());
        this.session = session;
        this.recipeStoreDir = recipeStoreDir;
        // Load any stored recipes and apply them
        this.loadRecipes();
        this.applyRecipes();
        // Schedule a period update task
        this.updateTask = new TimerTask() {
            public void run() {
                try
                {
                    applyRecipes();
                }
                catch (Exception e)
                {
                    logger.error("Failed to apply reciepes periodically", e);
                }
            }
        };
        this.updateTimer.scheduleAtFixedRate(this.updateTask, UPDATE_PERIOD, UPDATE_PERIOD);
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
    
    protected void storeRecipe(VPPRecipe recipe) throws IOException
    {
        this.writer.toFile(recipe, new File(this.recipeStoreDir, recipe.getName() + ".yaml"));
    }
    
    protected VPPRecipe loadRecipe(String name) throws IOException
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
                try
                {
                    VPPRecipe recipe = this.loadRecipe(name);
                    if (recipe != null)
                    {
                        this.recipes.put(name, recipe);
                        logger.info("Loaded recipe: " + name + " from " + file.getAbsolutePath());
                    }
                }
                catch (IOException e)
                {
                    logger.error("Failed to load recipe: " + name + " from " + file.getAbsolutePath() + ", ignoring this error for now.", e);
                }
            }
        }
    }
    
    protected void applyRecipes() throws InterruptedException, ExecutionException
    {
        synchronized (this.session)
        {
            Set<String> appliedRecipes = new HashSet<String>();
            List<VPPRecipe> toApply = new LinkedList<VPPRecipe>(this.recipes.values());
            // Apply all recipes following dependencies
            Collections.sort(toApply, (a, b) -> Integer.compare(a.getDependCount(), b.getDependCount()));
            for (VPPRecipe recipe : toApply)
            {
                this.applyRecipe(recipe, appliedRecipes);
            }
        }
    }
    
    protected void applyRecipe(VPPRecipe recipe, Set<String> appliedRecipes) throws InterruptedException, ExecutionException
    {
        synchronized (this.session)
        {
            if (! appliedRecipes.contains(recipe.getName()))
            {
                logger.info("Applying recipe " + recipe.getName() + " applying dependencies: " + recipe.getDepends());
                appliedRecipes.add(recipe.getName());
                // Apply dependencies
                for (String dependName : recipe.getDepends())
                {
                    VPPRecipe dependRecipe = this.recipes.get(dependName);
                    if (dependRecipe == null) throw new ExecutionException(new RuntimeException("No such dependency: " + dependName));
                    this.applyRecipe(dependRecipe, appliedRecipes);
                }
                // Apply this recipe
                try
                {
                    recipe.apply(this.session, this.createContext());
                }
                catch (InterruptedException | ExecutionException e)
                {
                    logger.error("Failed to apply recipe: " + recipe.getName(), e);
                    throw e;
                }
            }
        }
    }
    
    protected void applyRecipe(VPPRecipe recipe) throws InterruptedException, ExecutionException
    {
        this.applyRecipe(recipe, new HashSet<String>());
    }
    
    protected VPPRecipeContext createContext()
    {
        return new VPPRecipeContext()
        {
            public boolean hasDependency(String name)
            {
                return recipes.containsKey(name);
            }
            
            @SuppressWarnings("unchecked")
            public <T extends VPPRecipe> T getDependency(String name) throws ExecutionException
            {
                VPPRecipe recipe = recipes.get(name);
                if (recipe == null) throw new ExecutionException(new RuntimeException("No such dependency: " + name));
                return (T) recipe;
            }
        };
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
    
    @Override
    public Collection<? extends VPPRecipe> list(String type)
    {
        return this.recipes.values().stream().filter(i -> i.getType().equals(type)).collect(Collectors.toSet());
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
    public <T extends VPPRecipe> T remove(String name) throws InterruptedException, ExecutionException, IOException
    {
        Objects.requireNonNull(name);
        VPPRecipe recipe = this.recipes.remove(name);
        // TODO: unapply
        this.deleteRecipe(name);
        return (T) recipe;
    }

    public VPPSimple vpp()
    {
        return this.session;
    }
    
    public void close() throws IOException
    {
        // Cancel our update task
        this.updateTask.cancel();
        this.updateTimer.cancel();
        // Close down the VPP session
        synchronized (this.session)
        {
            this.session.close();
        }
    }
}
