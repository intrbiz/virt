package com.intrbiz.virt.model.dns;

public class DNSResult
{
    private String qtype;
    
    private String qname;
    
    private String content;
    
    private int ttl = 600;
    
    private int domain_id = -1;
    
    public DNSResult()
    {
        super();
    }

    public DNSResult(String qtype, String qname, String content, int ttl)
    {
        super();
        this.qtype = qtype;
        this.qname = qname;
        this.content = content;
        this.ttl = ttl;
    }

    public String getQtype()
    {
        return qtype;
    }

    public void setQtype(String qtype)
    {
        this.qtype = qtype;
    }

    public String getQname()
    {
        return qname;
    }

    public void setQname(String qname)
    {
        this.qname = qname;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getTtl()
    {
        return ttl;
    }

    public void setTtl(int ttl)
    {
        this.ttl = ttl;
    }

    public int getDomain_id()
    {
        return domain_id;
    }

    public void setDomain_id(int domain_id)
    {
        this.domain_id = domain_id;
    }
}
