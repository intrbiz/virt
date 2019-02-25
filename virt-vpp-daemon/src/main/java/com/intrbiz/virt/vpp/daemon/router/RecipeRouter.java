package com.intrbiz.virt.vpp.daemon.router;

import java.util.Set;
import java.util.stream.Collectors;

import com.intrbiz.metadata.Delete;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.YAML;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.recipe.Bridge;
import com.intrbiz.vpp.recipe.BridgeInterface;
import com.intrbiz.vpp.recipe.HostInterface;
import com.intrbiz.vpp.recipe.VXLANTunnel;
import com.intrbiz.vpp.recipe.VethHostInterface;
import com.intrbiz.vpp.recipe.VhostUserInterface;

@Prefix("/recipe")
public class RecipeRouter extends VppBaseRouter
{
    @Get("/")
    @YAML
    public Set<String> getRecipes() throws Exception
    {
        return recipeManager().list().stream().map(VPPRecipe::getName).collect(Collectors.toSet());
    }
    
    @Post("/")
    @YAML
    public VPPRecipe applyRecipe(@YAML({ 
        Bridge.class, HostInterface.class, VethHostInterface.class, 
        VhostUserInterface.class, BridgeInterface.class, VXLANTunnel.class 
    }) VPPRecipe recipe) throws Exception
    {
        return recipeManager().update(recipe);
    }
    
    @Get("/type/:type")
    @YAML
    public Set<String> getRecipes(String type) throws Exception
    {
        return recipeManager().list(type).stream().map(VPPRecipe::getName).collect(Collectors.toSet());
    }
    
    @Get("/name/:name")
    @YAML
    public VPPRecipe getRecipe(String name) throws Exception
    {
        return recipeManager().get(name);
    }
    
    @Delete("/name/:name")
    @YAML
    public VPPRecipe removeRecipe(String name) throws Exception
    {
        return recipeManager().remove(name);
    }
}
