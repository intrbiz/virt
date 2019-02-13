package com.intrbiz.virt.vpp.call;

import java.io.IOException;

import org.apache.http.client.fluent.Response;

import com.intrbiz.virt.vpp.BaseVPPDaemonClient;
import com.intrbiz.virt.vpp.VPPDaemonAPICall;
import com.intrbiz.virt.vpp.VPPDaemonClientAPIException;

public class AliveCall extends VPPDaemonAPICall<String>
{    
    public AliveCall(BaseVPPDaemonClient client)
    {
        super(client);
    }
    
    public String execute()
    {
        try
        {
            Response response = execute(get(url("/health/alive")));
            return asString(response);
        }
        catch (IOException e)
        {
            throw new VPPDaemonClientAPIException("Error calling alive", e);
        }
    }
}
