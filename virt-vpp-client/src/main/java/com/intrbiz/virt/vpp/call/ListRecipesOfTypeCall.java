package com.intrbiz.virt.vpp.call;

import java.io.IOException;
import java.util.Set;

import org.apache.http.client.fluent.Response;

import com.intrbiz.virt.vpp.BaseVPPDaemonClient;
import com.intrbiz.virt.vpp.VPPDaemonAPICall;
import com.intrbiz.virt.vpp.VPPDaemonClientAPIException;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipeBase;

public class ListRecipesOfTypeCall extends VPPDaemonAPICall<Set<String>>
{   
    private String type;
    
    public ListRecipesOfTypeCall(BaseVPPDaemonClient client)
    {
        super(client);
    }
    
    public ListRecipesOfTypeCall type(String type)
    {
        this.type = type;
        return this;
    }
    
    public ListRecipesOfTypeCall type(Class<? extends VPPRecipe> type)
    {
        this.type = VPPRecipeBase.getType(type);
        return this;
    }
    
    public Set<String> execute()
    {
        try
        {
            Response response = execute(get(url("/recipe/type/" + this.type)));
            return fromYaml(response, setOf(String.class));
        }
        catch (IOException e)
        {
            throw new VPPDaemonClientAPIException("Error listing recipes of type " + this.type, e);
        }
    }
}
