package com.intrbiz.vpp.api.model;

import java.io.Serializable;
import java.security.SecureRandom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToIntConverter;
import com.intrbiz.vpp.api.model.VNI.VNIFromIntConverter;
import com.intrbiz.vpp.api.util.IntValue;
import com.intrbiz.vpp.util.HexUtil;

/**
 * A VxLAN Network Identifier
 */
@JsonDeserialize(converter = VNIFromIntConverter.class)
@JsonSerialize(converter = ToIntConverter.class)
public final class VNI implements Comparable<VNI>, Serializable, IntValue
{
    public static final int MASK = 0x00FFFFFF;
    
    private static final long serialVersionUID = 1L;
    
    private final int value;

    public VNI(int value)
    {
        super();
        this.value = value & MASK;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public int compareTo(VNI o)
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
        VNI other = (VNI) obj;
        if (value != other.value) return false;
        return true;
    }

    public String toString()
    {
        return "VNI[" + this.value + "]";
    }
    
    public String asHex()
    {
        return HexUtil.u24Hex(this.value);
    }
    
    public static VNI random()
    {
        return new VNI((new SecureRandom()).nextInt());
    }
    
    public static class VNIFromIntConverter extends StdConverter<Integer, VNI>
    {
        public VNI convert(Integer value)
        {
            return value == null ? null : new VNI(value.intValue());
        }
    }
}
