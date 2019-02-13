package com.intrbiz.vpp.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToIntConverter;
import com.intrbiz.vpp.api.model.InterfaceIndex.InterfaceIndexFromIntConverter;
import com.intrbiz.vpp.api.util.IntValue;

@JsonDeserialize(converter = InterfaceIndexFromIntConverter.class)
@JsonSerialize(converter = ToIntConverter.class)
public final class InterfaceIndex implements Comparable<InterfaceIndex>, Serializable, IntValue
{
    private static final long serialVersionUID = 1L;
    
    private final int value;

    public InterfaceIndex(int value)
    {
        super();
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public int compareTo(InterfaceIndex o)
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
        InterfaceIndex other = (InterfaceIndex) obj;
        if (value != other.value) return false;
        return true;
    }

    public String toString()
    {
        return "Interface[" + this.value + "]";
    }
    
    public static class InterfaceIndexFromIntConverter extends StdConverter<Integer, InterfaceIndex>
    {
        public InterfaceIndex convert(Integer value)
        {
            return value == null ? null : new InterfaceIndex(value.intValue());
        }
    }
}
