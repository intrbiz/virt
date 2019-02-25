package com.intrbiz.virt.vpp;

import com.intrbiz.virt.vpp.call.GetRecipeCall;
import com.intrbiz.virt.vpp.call.ListRecipesCall;
import com.intrbiz.virt.vpp.call.ListRecipesOfTypeCall;
import com.intrbiz.virt.vpp.call.UpdateRecipeCall;

public class VPPDaemonClient extends BaseVPPDaemonClient
{
    public VPPDaemonClient(String baseURL)
    {
        super(baseURL);
    }
    
    // Recipe calls
    
    public ListRecipesCall callListRecipes()
    {
        return new ListRecipesCall(this);
    }
    
    public ListRecipesOfTypeCall callListRecipesOfType()
    {
        return new ListRecipesOfTypeCall(this);
    }
    
    public GetRecipeCall callGetRecipe()
    {
        return new GetRecipeCall(this);
    }
    
    public UpdateRecipeCall callUpdateRecipe()
    {
        return new UpdateRecipeCall(this);
    }
}