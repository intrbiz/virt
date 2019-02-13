package com.intrbiz.vpp.api.model;

import java.io.Serializable;
import java.security.SecureRandom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToIntConverter;
import com.intrbiz.vpp.api.model.VRFIndex.VRFIndexFromIntConverter;
import com.intrbiz.vpp.api.util.IntValue;

@JsonDeserialize(converter = VRFIndexFromIntConverter.class)
@JsonSerialize(converter = ToIntConverter.class)
public final class VRFIndex implements Comparable<VRFIndex>, Serializable, IntValue
{
    private static final long serialVersionUID = 1L;
    
    private final int value;

    public VRFIndex(int value)
    {
        super();
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public int compareTo(VRFIndex o)
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
        VRFIndex other = (VRFIndex) obj;
        if (value != other.value) return false;
        return true;
    }

    public String toString()
    {
        return "VRF[" + this.value + "]";
    }
    
    public static final VRFIndex random()
    {
        return new VRFIndex(new SecureRandom().nextInt());
    }
    
    public static class VRFIndexFromIntConverter extends StdConverter<Integer, VRFIndex>
    {
        public VRFIndex convert(Integer value)
        {
            return value == null ? null : new VRFIndex(value.intValue());
        }
    }
}
