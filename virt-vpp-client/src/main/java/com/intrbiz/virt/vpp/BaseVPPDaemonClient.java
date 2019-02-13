package com.intrbiz.virt.vpp;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.intrbiz.Util;
import com.intrbiz.virt.vpp.call.AliveCall;

public abstract class BaseVPPDaemonClient
{    
    // the API base url
    protected final String baseURL;
    
    // customised HTTPClient
    protected Registry<ConnectionSocketFactory> schemeRegistry;
    
    protected PoolingHttpClientConnectionManager connectionManager;
    
    protected HttpClient client;
    
    protected Executor executor;
    
    public BaseVPPDaemonClient(String baseURL)
    {
        super();
        // remove trailing /
        if (baseURL.endsWith("/"))
        {
            baseURL = baseURL.substring(0, baseURL.length() - 1);
        }
        this.baseURL = baseURL;
        // setup our executor
        // scheme registry
        this.schemeRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
         .register("http",  PlainConnectionSocketFactory.getSocketFactory())
         .build();
        // create out connection manager
        this.connectionManager = new PoolingHttpClientConnectionManager(this.schemeRegistry);
        this.connectionManager.setDefaultMaxPerRoute(5);
        this.connectionManager.setMaxTotal(10);
        // create our client
        this.client = HttpClientBuilder.create().setConnectionManager(this.connectionManager).build();
        // create our executor
        this.executor = Executor.newInstance(this.client);
    }
    
    // HTTP Client Executor
    
    public Executor executor()
    {
        return this.executor;
    }
    
    // the base url
    
    public String getBaseURL()
    {
        return this.baseURL;
    }
    
    // create a full URL from the following path element
    
    public String url(String... urlElements)
    {
        StringBuilder sb = new StringBuilder(this.baseURL);
        for (String urlElement : urlElements)
        {
            sb.append(urlElement);
        }
        return sb.toString();
    }
    
    public String appendQuery(String url, NameValuePair... parameters)
    {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        boolean ns = false;
        for (NameValuePair parameter : parameters)
        {
            if (ns) sb.append("&");
            sb.append(Util.urlEncode(parameter.getName(), Util.UTF8)).append("=").append(Util.urlEncode(parameter.getValue(), Util.UTF8));
            ns = true;
        }
        return sb.toString();
    }
    
    public String appendQuery(String url, Iterable<NameValuePair> parameters)
    {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        boolean ns = false;
        for (NameValuePair parameter : parameters)
        {
            if (ns) sb.append("&");
            sb.append(Util.urlEncode(parameter.getName(), Util.UTF8)).append("=").append(Util.urlEncode(parameter.getValue(), Util.UTF8));
            ns = true;
        }
        return sb.toString();
    }
    
    // basic calls
    
    public AliveCall callAlive()
    {
        return new AliveCall(this);
    }
}
