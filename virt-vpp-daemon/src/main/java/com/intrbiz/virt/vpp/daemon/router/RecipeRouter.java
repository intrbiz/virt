package com.intrbiz.virt.vpp.daemon.router;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import com.intrbiz.metadata.Delete;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.YAML;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.recipe.Bridge;
import com.intrbiz.vpp.recipe.HostInterface;
import com.intrbiz.vpp.recipe.VMBridges;
import com.intrbiz.vpp.recipe.VMHost;
import com.intrbiz.vpp.recipe.VMInterconnect;
import com.intrbiz.vpp.recipe.VMInterface;
import com.intrbiz.vpp.recipe.VMMetadata;
import com.intrbiz.vpp.recipe.VMMetadataBridge;
import com.intrbiz.vpp.recipe.VMNetworks;
import com.intrbiz.vpp.recipe.VXLANMesh;
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
        return recipeManager().list().stream().map(Entry::getKey).collect(Collectors.toSet());
    }
    
    @Get("/name/:name")
    @YAML
    public VPPRecipe getRecipe(String name) throws Exception
    {
        return recipeManager().get(name);
    }
    
    @Post("/name/:name")
    @YAML
    public VPPRecipe applyRecipe(String name, @YAML({ Bridge.class, HostInterface.class, VethHostInterface.class, VhostUserInterface.class, VMBridges.class, VMHost.class, VMMetadata.class, VMNetworks.class, VMInterconnect.class,
                                                       VMInterface.class, VMMetadataBridge.class, VXLANMesh.class, VXLANTunnel.class }) VPPRecipe recipe) throws Exception
    {
        return recipeManager().update(name, recipe);
    }
    
    @Delete("/name/:name")
    @YAML
    public VPPRecipe removeRecipe(String name) throws Exception
    {
        return recipeManager().remove(name);
    }
}
