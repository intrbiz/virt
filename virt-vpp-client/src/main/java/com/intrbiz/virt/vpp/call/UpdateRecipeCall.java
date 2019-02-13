package com.intrbiz.virt.vpp.call;

import java.io.IOException;

import org.apache.http.client.fluent.Response;

import com.intrbiz.virt.vpp.BaseVPPDaemonClient;
import com.intrbiz.virt.vpp.VPPDaemonAPICall;
import com.intrbiz.virt.vpp.VPPDaemonClientAPIException;
import com.intrbiz.vpp.api.recipe.VPPRecipe;

public class UpdateRecipeCall extends VPPDaemonAPICall<VPPRecipe>
{   
    private String name;
    
    private VPPRecipe recipe;
    
    public UpdateRecipeCall(BaseVPPDaemonClient client)
    {
        super(client);
    }
    
    public UpdateRecipeCall name(String name)
    {
        this.name = name;
        return this;
    }
    
    public UpdateRecipeCall recipe(VPPRecipe recipe)
    {
        this.recipe = recipe;
        return this;
    }
    
    public VPPRecipe execute()
    {
        try
        {
            Response response = execute(bodyRecipe(post(url("/recipe/name/" + this.name)), this.recipe));
            return asVPPRecipe(response);
        }
        catch (IOException e)
        {
            throw new VPPDaemonClientAPIException("Error applying recipe " + this.name, e);
        }
    }
}
