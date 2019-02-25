package com.intrbiz.virt.vpp.call;

import java.io.IOException;
import java.util.Set;

import org.apache.http.client.fluent.Response;

import com.intrbiz.virt.vpp.BaseVPPDaemonClient;
import com.intrbiz.virt.vpp.VPPDaemonAPICall;
import com.intrbiz.virt.vpp.VPPDaemonClientAPIException;

public class ListRecipesCall extends VPPDaemonAPICall<Set<String>>
{       
    public ListRecipesCall(BaseVPPDaemonClient client)
    {
        super(client);
    }
    
    public Set<String> execute()
    {
        try
        {
            Response response = execute(get(url("/recipe/")));
            return fromYaml(response, setOf(String.class));
        }
        catch (IOException e)
        {
            throw new VPPDaemonClientAPIException("Error listing recipes", e);
        }
    }
}
