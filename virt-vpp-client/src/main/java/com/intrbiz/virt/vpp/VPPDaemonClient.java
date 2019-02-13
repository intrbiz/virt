package com.intrbiz.virt.vpp;

import com.intrbiz.virt.vpp.call.UpdateRecipeCall;
import com.intrbiz.virt.vpp.call.GetRecipeCall;

public class VPPDaemonClient extends BaseVPPDaemonClient
{
    public VPPDaemonClient(String baseURL)
    {
        super(baseURL);
    }
    
    // Recipe calls
    
    public GetRecipeCall callGetRecipe()
    {
        return new GetRecipeCall(this);
    }
    
    public UpdateRecipeCall callApplyRecipe()
    {
        return new UpdateRecipeCall(this);
    }
}