package com.intrbiz.vpp.api.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToStringConverter;
import com.intrbiz.vpp.api.model.IPv4Address.IPv4AddressFromStringConverter;
import com.intrbiz.vpp.util.HexUtil;

@JsonDeserialize(converter = IPv4AddressFromStringConverter.class)
@JsonSerialize(converter = ToStringConverter.class)
public class IPv4Address implements Comparable<IPv4Address>, Serializable
{
    private static final long serialVersionUID = 1L;

    private static final Pattern IPV4_FORMAT = Pattern.compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})");
    
    public static final int IPV4_LENGTH = 4;
    
    private final byte[] value;

    public IPv4Address(byte[] value)
    {
        super();
        this.value = Objects.requireNonNull(value);
        if (this.value.length != IPV4_LENGTH) throw new IllegalArgumentException("IPv4 address must be " + IPV4_LENGTH + " bytes");
    }

    public byte[] getValue()
    {
        return this.value;
    }
    
    public IPv4Address applyNetmask(IPv4Address netmask)
    {
        return this.and(netmask);
    }
    
    public int getBits()
    {
        return pop();
    }

    int asInt()
    {
        return this.value[0] << 24 | this.value[1] << 16 | this.value[2] << 8 | this.value[3];
    }
    
    int pop()
    {
        return pop(this.asInt());
    }
    
    IPv4Address and(IPv4Address other)
    {
        byte[] result = new byte[IPV4_LENGTH];
        for (int i = 0; i < IPV4_LENGTH; i++)
        {
            result[i] = (byte) ((this.value[i] & 0xFF) & (other.value[i] & 0xFF));
        }
        return new IPv4Address(result);
    }
    
    IPv4Address or(IPv4Address other)
    {
        byte[] result = new byte[IPV4_LENGTH];
        for (int i = 0; i < IPV4_LENGTH; i++)
        {
            result[i] = (byte) ((this.value[i] & 0xFF) | (other.value[i] & 0xFF));
        }
        return new IPv4Address(result);
    }
    
    IPv4Address not()
    {
        byte[] result = new byte[IPV4_LENGTH];
        for (int i = 0; i < IPV4_LENGTH; i++)
        {
            result[i] = (byte) (~ (this.value[i] & 0xFF));
        }
        return new IPv4Address(result);
    }

    @Override
    public int compareTo(IPv4Address o)
    {
        return Integer.compareUnsigned(this.asInt(), o.asInt());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IPv4Address other = (IPv4Address) obj;
        if (!Arrays.equals(value, other.value)) return false;
        return true;
    }

    public String toString()
    {
        return (this.value[0] & 0xFF) + "." + (this.value[1] & 0xFF) + "." + (this.value[2] & 0xFF) + "." + (this.value[3] & 0xFF);
    }
    
    public String asHex()
    {
        return HexUtil.u32Hex(this.asInt());
    }
    
    public static IPv4Address fromString(String address)
    {
        Matcher match = IPV4_FORMAT.matcher(Objects.requireNonNull(address));
        if (! match.matches()) throw new IllegalArgumentException("Invalid IPv4 address");
        byte[] addressBytes = new byte[IPV4_LENGTH];
        for (int i = 0; i < addressBytes.length; i++)
        {
            int value = Integer.parseInt(match.group(1 + i));
            if (value < 0 || value > 255) throw new IllegalArgumentException("Invalid IPv4 address");
            addressBytes[i] = (byte) value;
        }
        return new IPv4Address(addressBytes);
    }
    
    /*
     * Count the number of 1-bits in a 32-bit integer using a divide-and-conquer strategy
     * see Hacker's Delight section 5.1
     */
    private static int pop(int x) {
        x = x - ((x >>> 1) & 0x55555555);
        x = (x & 0x33333333) + ((x >>> 2) & 0x33333333);
        x = (x + (x >>> 4)) & 0x0F0F0F0F;
        x = x + (x >>> 8);
        x = x + (x >>> 16);
        return x & 0x0000003F;
    }
    
    public static class IPv4AddressFromStringConverter extends StdConverter<String, IPv4Address>
    {
        public IPv4Address convert(String value)
        {
            return value == null ? null : IPv4Address.fromString(value);
        }
    }
}
