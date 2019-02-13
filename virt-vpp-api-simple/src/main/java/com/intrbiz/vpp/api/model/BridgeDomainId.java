package com.intrbiz.vpp.api.model;

import java.io.Serializable;
import java.security.SecureRandom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToIntConverter;
import com.intrbiz.vpp.api.model.BridgeDomainId.BridgeDomainIdFromIntConverter;
import com.intrbiz.vpp.api.util.IntValue;
import com.intrbiz.vpp.util.HexUtil;

@JsonDeserialize(converter = BridgeDomainIdFromIntConverter.class)
@JsonSerialize(converter = ToIntConverter.class)
public final class BridgeDomainId implements Comparable<BridgeDomainId>, Serializable, IntValue
{
    private static final long serialVersionUID = 1L;
    
    private final int value;

    public BridgeDomainId(int value)
    {
        super();
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public int compareTo(BridgeDomainId o)
    {
        return Integer.compare(this.value, o.value);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BridgeDomainId other = (BridgeDomainId) obj;
        if (value != other.value) return false;
        return true;
    }

    public String toString()
    {
        return "BridgeDomain[" + this.value + "]";
    }
    
    public String asHex()
    {
        return HexUtil.u32Hex(this.value);
    }
    
    public static BridgeDomainId random()
    {
        return new BridgeDomainId((new SecureRandom()).nextInt() & 0x7FFFFFFF);
    }
    
    public static class BridgeDomainIdFromIntConverter extends StdConverter<Integer, BridgeDomainId>
    {
        public BridgeDomainId convert(Integer value)
        {
            return value == null ? null : new BridgeDomainId(value.intValue());
        }
    }
}
