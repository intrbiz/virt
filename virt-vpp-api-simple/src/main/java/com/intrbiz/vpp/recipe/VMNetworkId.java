package com.intrbiz.vpp.recipe;

import java.io.Serializable;
import java.security.SecureRandom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToIntConverter;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.VNI;
import com.intrbiz.vpp.api.util.IntValue;
import com.intrbiz.vpp.recipe.VMNetworkId.VMNetworkIdFromIntConverter;
import com.intrbiz.vpp.util.HexUtil;

@JsonDeserialize(converter = VMNetworkIdFromIntConverter.class)
@JsonSerialize(converter = ToIntConverter.class)
public class VMNetworkId implements Comparable<VMNetworkId>, Serializable, IntValue
{
    public static final int MASK = 0x00FFFFFF;
    
    private static final long serialVersionUID = 1L;
    
    private final int value;

    public VMNetworkId(int value)
    {
        super();
        this.value = value & MASK;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public int compareTo(VMNetworkId o)
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
        VMNetworkId other = (VMNetworkId) obj;
        if (value != other.value) return false;
        return true;
    }

    public String toString()
    {
        return "VMNetwork[" + this.value + "]";
    }
    
    public String asHex()
    {
        return HexUtil.u24Hex(this.value);
    }
    
    public VNI toVNI() {
        return new VNI(this.value);
    }
    
    public BridgeDomainId toBridgeDomain() {
        return new BridgeDomainId(this.value);
    }
    
    public static VMNetworkId random()
    {
        return new VMNetworkId(new SecureRandom().nextInt() + 128);
    }
    
    public static VMNetworkId fromVNI(VNI vni)
    {
        return new VMNetworkId(vni.getValue());
    }
    
    public static VMNetworkId fromBridgeDomain(BridgeDomainId bdid)
    {
        return new VMNetworkId(bdid.getValue());
    }
    
    public static class VMNetworkIdFromIntConverter extends StdConverter<Integer, VMNetworkId>
    {
        public VMNetworkId convert(Integer value)
        {
            return value == null ? null : new VMNetworkId(value.intValue());
        }
    }
}
