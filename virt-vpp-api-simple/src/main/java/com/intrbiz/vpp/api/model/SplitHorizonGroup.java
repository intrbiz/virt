package com.intrbiz.vpp.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToByteConverter;
import com.intrbiz.vpp.api.model.SplitHorizonGroup.SplitHorizonGroupFromByteConverter;
import com.intrbiz.vpp.api.util.ByteValue;

@JsonDeserialize(converter = SplitHorizonGroupFromByteConverter.class)
@JsonSerialize(converter = ToByteConverter.class)
public final class SplitHorizonGroup implements Comparable<SplitHorizonGroup>, Serializable, ByteValue
{
    public static final SplitHorizonGroup DEFAULT = new SplitHorizonGroup(0);
    
    public static final SplitHorizonGroup ONE = new SplitHorizonGroup(1);
    
    public static final SplitHorizonGroup TWO = new SplitHorizonGroup(2);
    
    public static final SplitHorizonGroup THREE = new SplitHorizonGroup(3);
    
    public static final SplitHorizonGroup FOUR = new SplitHorizonGroup(4);
    
    public static final SplitHorizonGroup FIVE = new SplitHorizonGroup(5);
    
    public static final SplitHorizonGroup SIX = new SplitHorizonGroup(6);
    
    public static final SplitHorizonGroup SEVEN = new SplitHorizonGroup(7);
    
    public static final SplitHorizonGroup EIGTH = new SplitHorizonGroup(8);
    
    public static final SplitHorizonGroup NINE = new SplitHorizonGroup(9);
    
    private static final long serialVersionUID = 1L;
    
    private final byte value;

    public SplitHorizonGroup(int value)
    {
        super();
        if (value < 0) throw new IllegalArgumentException("Value must be >= 0");
        if (value > 255) throw new IllegalArgumentException("Value must be <= 255");
        this.value = (byte) value;
    }

    public byte getValue()
    {
        return value;
    }

    @Override
    public int compareTo(SplitHorizonGroup o)
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
        SplitHorizonGroup other = (SplitHorizonGroup) obj;
        if (value != other.value) return false;
        return true;
    }

    public String toString()
    {
        return "SHG[" + this.value + "]";
    }
    
    public static class SplitHorizonGroupFromByteConverter extends StdConverter<Byte, SplitHorizonGroup>
    {
        public SplitHorizonGroup convert(Byte value)
        {
            return value == null ? null : new SplitHorizonGroup(value.byteValue());
        }
    }
}
