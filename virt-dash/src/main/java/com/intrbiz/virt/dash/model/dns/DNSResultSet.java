package com.intrbiz.virt.dash.model.dns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DNSResultSet
{    
    @SuppressWarnings("unchecked")
    public static final DNSResultSet EMPTY = new DNSResultSet(Collections.EMPTY_LIST);
    
    public static final DNSResultSet NULL = new DNSResultSet((List<DNSResult>) null);
    
    private List<DNSResult> result;
    
    public DNSResultSet()
    {
        super();
        this.result = new ArrayList<DNSResult>();
    }
    
    public DNSResultSet(DNSResult... results)
    {
        super();
        this.result = new ArrayList<DNSResult>();
        Collections.addAll(this.result, results);
    }
    
    public DNSResultSet(List<DNSResult> result)
    {
        super();
        this.result = result;
    }

    public List<DNSResult> getResult()
    {
        return result;
    }

    public void setResult(List<DNSResult> result)
    {
        this.result = result;
    }
    
    public DNSResultSet add(DNSResult result)
    {
        this.result.add(result);
        return this;
    }
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public String toString()
    {
        try
        {
            return MAPPER.writeValueAsString(this);
        }
        catch (JsonProcessingException e)
        {
        }
        return "";
    }
}
