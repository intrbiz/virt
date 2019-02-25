package com.intrbiz.virt.vpp;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.JavaType;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeReader;
import com.intrbiz.vpp.util.RecipeWriter;

public abstract class VPPDaemonAPICall<T>
{
    private BaseVPPDaemonClient client;
    
    public VPPDaemonAPICall(BaseVPPDaemonClient client)
    {
        this.client = client;
    }
    
    protected BaseVPPDaemonClient client()
    {
        return this.client;
    }
    
    protected String url(String... urlElements)
    {
        return this.client.url(urlElements);
    }
    
    protected String appendQuery(String url, NameValuePair... parameters)
    {
        return this.client.appendQuery(url, parameters);
    }
    
    protected String appendQuery(String url, Iterable<NameValuePair> parameters)
    {
        return this.client.appendQuery(url, parameters);
    }
    
    protected NameValuePair param(String name, String value)
    {
        return new BasicNameValuePair(name, value);
    }
    
    protected NameValuePair param(String name, Object value)
    {
        return new BasicNameValuePair(name, String.valueOf(value));
    }
    
    protected Response execute(Request request) throws ClientProtocolException, IOException
    {
        return this.client.executor().execute(request);
    }
    
    protected Request get(String url)
    {
        return Request.Get(url);
    }
    
    protected Request post(String url)
    {
        return Request.Post(url);
    }
    
    protected Request put(String url)
    {
        return Request.Put(url);
    }
    
    protected Request delete(String url)
    {
        return Request.Delete(url);
    }
    
    protected Request bodyRecipe(Request request, VPPRecipe recipe)
    {
        return request.bodyString(RecipeWriter.getDefault().toString(recipe), ContentType.create("text/yaml", Consts.UTF_8));
    }
    
    protected String asString(Response response) throws IOException
    {
        return response.returnContent().asString();
    }
    
    protected VPPRecipe asVPPRecipe(Response response) throws IOException
    {
        return RecipeReader.getDefault().fromString(VPPRecipe.class, asString(response));
    }
    
    protected <R> R fromYaml(Response response, Class<R> type) throws IOException
    {
        return this.client.getYamlMapper().readValue(asString(response), type);
    }
    
    @SuppressWarnings("unchecked")
    protected <R> R fromYaml(Response response, JavaType type) throws IOException
    {
        return (R) this.client.getYamlMapper().readValue(asString(response), type);
    }
    
    @SuppressWarnings("rawtypes")
    protected JavaType collectionType(Class<? extends Collection> collectionType, Class<?> elementType)
    {
        return this.client.getYamlMapper().getTypeFactory().constructCollectionType(collectionType, elementType);
    }
    
    protected JavaType setOf(Class<?> elementType)
    {
        return this.collectionType(Set.class, elementType);
    }
    
    protected JavaType listOf(Class<?> elementType)
    {
        return this.collectionType(Set.class, elementType);
    }
    
    /**
     * Execute this call
     */
    public abstract T execute() throws VPPDaemonClientAPIException;
}
