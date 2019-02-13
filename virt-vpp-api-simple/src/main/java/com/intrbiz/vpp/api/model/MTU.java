package com.intrbiz.vpp.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToShortConverter;
import com.intrbiz.vpp.api.model.MTU.MTUFromShortConverter;
import com.intrbiz.vpp.api.util.ShortValue;

@JsonDeserialize(converter = MTUFromShortConverter.class)
@JsonSerialize(converter = ToShortConverter.class)
public final class MTU implements Comparable<MTU>, Serializable, ShortValue
{
    public static final MTU DEFAULT = new MTU(1500);
    
    public static final MTU JUMBO = new MTU(9000);
    
    private static final long serialVersionUID = 1L;
    
    private final short value;

    public MTU(int value)
    {
        super();
        if (value < 0) throw new IllegalArgumentException("Value must be >= 0");
        if (value > 65535) throw new IllegalArgumentException("Value must be <= 65535");
        this.value = (short) value;
    }

    public short getValue()
    {
        return value;
    }
    
    public String getValueAsString()
    {
        return String.valueOf(this.value);
    }

    @Override
    public int compareTo(MTU o)
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
        MTU other = (MTU) obj;
        if (value != other.value) return false;
        return true;
    }

    public String toString()
    {
        return "MTU[" + this.value + "]";
    }
    
    public static class MTUFromShortConverter extends StdConverter<Short, MTU>
    {
        public MTU convert(Short value)
        {
            return value == null ? null : new MTU(value.shortValue());
        }
    }
}
