package com.intrbiz.virt.model.metadata.network.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RouteV2
{
    @JsonProperty("to")
    private String to;
    
    @JsonProperty("via")
    private String via;
    
    @JsonProperty("metric")
    private int metric;

    public RouteV2()
    {
        super();
    }
    
    public RouteV2(String to, String via, int metric)
    {
        this();
        this.to = to;
        this.via = via;
        this.metric = metric;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getVia()
    {
        return via;
    }

    public void setVia(String via)
    {
        this.via = via;
    }

    public int getMetric()
    {
        return metric;
    }

    public void setMetric(int metric)
    {
        this.metric = metric;
    }
}
