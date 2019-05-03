package com.intrbiz.virt.dash.model.balancer;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.virt.model.LoadBalancer;
import com.intrbiz.virt.model.LoadBalancerPool;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("load_balancer.configuration")
public class LoadBalancerConfiguration
{
    private String node;
    
    private String hostedDomain;
    
    private LoadBalancerPool pool;
    
    private List<LoadBalancer> loadBalancers = new LinkedList<LoadBalancer>();
    
    public LoadBalancerConfiguration()
    {
        super();
    }
    
    public LoadBalancerConfiguration(LoadBalancerPool pool, String hostedDomain, String node)
    {
        super();
        this.pool = pool;
        this.hostedDomain = hostedDomain;
        this.node = node;
    }
    
    

    public String getHostedDomain()
    {
        return hostedDomain;
    }

    public void setHostedDomain(String hostedDomain)
    {
        this.hostedDomain = hostedDomain;
    }

    public String getNode()
    {
        return node;
    }

    public void setNode(String node)
    {
        this.node = node;
    }

    public LoadBalancerPool getPool()
    {
        return pool;
    }

    public void setPool(LoadBalancerPool pool)
    {
        this.pool = pool;
    }

    public List<LoadBalancer> getLoadBalancers()
    {
        return loadBalancers;
    }

    public void setLoadBalancers(List<LoadBalancer> loadBalancers)
    {
        this.loadBalancers = loadBalancers;
    }
    
    public void addLoadBalancer(LoadBalancer loadBalancer)
    {
        this.loadBalancers.add(loadBalancer);
    }
}
