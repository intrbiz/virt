package com.intrbiz.virt.vpp.call;

import java.io.IOException;

import org.apache.http.client.fluent.Response;

import com.intrbiz.virt.vpp.BaseVPPDaemonClient;
import com.intrbiz.virt.vpp.VPPDaemonAPICall;
import com.intrbiz.virt.vpp.VPPDaemonClientAPIException;
import com.intrbiz.vpp.api.recipe.VPPRecipe;

public class GetRecipeCall extends VPPDaemonAPICall<VPPRecipe>
{   
    private String name;
    
    public GetRecipeCall(BaseVPPDaemonClient client)
    {
        super(client);
    }
    
    public GetRecipeCall name(String name)
    {
        this.name = name;
        return this;
    }
    
    public VPPRecipe execute()
    {
        try
        {
            Response response = execute(get(url("/recipe/name/" + this.name)));
            return asVPPRecipe(response);
        }
        catch (IOException e)
        {
            throw new VPPDaemonClientAPIException("Error getting recipe " + this.name, e);
        }
    }
}
