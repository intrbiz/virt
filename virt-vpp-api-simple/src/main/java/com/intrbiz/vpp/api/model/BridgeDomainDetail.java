package com.intrbiz.vpp.api.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("detail.bridge")
public class BridgeDomainDetail implements Comparable<BridgeDomainDetail>
{
    private BridgeDomainId id;
    
    public BridgeDomainDetail()
    {
        super();
    }

    public BridgeDomainId getId()
    {
        return id;
    }

    public void setId(BridgeDomainId id)
    {
        this.id = id;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BridgeDomainDetail other = (BridgeDomainDetail) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public int compareTo(BridgeDomainDetail o)
    {
        return this.id.compareTo(o.id);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
