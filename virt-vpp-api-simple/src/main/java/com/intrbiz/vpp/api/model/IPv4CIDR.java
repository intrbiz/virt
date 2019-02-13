package com.intrbiz.vpp.api.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToStringConverter;
import com.intrbiz.vpp.api.model.IPv4CIDR.IPv4CIDRFromStringConverter;

@JsonDeserialize(converter = IPv4CIDRFromStringConverter.class)
@JsonSerialize(converter = ToStringConverter.class)
public class IPv4CIDR implements Comparable<IPv4CIDR>, Serializable
{
    private static final long serialVersionUID = 1L;

    private static final Pattern IPV4_CIDR_FORMAT = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})/([0-9]{1,2})");

    private final IPv4Address address;

    private final int maskBits;
    
    private IPv4Address netmask;
    
    private IPv4Address network;
    
    private IPv4Address broadcast;

    public IPv4CIDR(IPv4Address address, int maskBits)
    {
        super();
        this.address = Objects.requireNonNull(address);
        this.maskBits = maskBits;
        if (this.maskBits < 0 || this.maskBits > 32) throw new IllegalArgumentException("Invalid IPv4 CIDR mask bits");
    }

    public IPv4Address getAddress()
    {
        return address;
    }

    public int getMaskBits()
    {
        return maskBits;
    }
    
    public IPv4Address getNetmask()
    {
        if (this.netmask == null)
        {
            byte[] mask = new byte[IPv4Address.IPV4_LENGTH];
            for (int bits = (this.maskBits - 1); bits >= 0; bits--)
            {
                mask[bits / 8] |= 1 << (bits % 8);
            }
            this.netmask = new IPv4Address(mask);
        }
        return this.netmask;
    }
    
    public IPv4Address getNetwork()
    {
        if (this.network == null)
        {
            IPv4Address netmask = this.getNetmask();
            this.network = this.address.and(netmask);
        }
        return this.network;
    }
    
    public IPv4Address getBroadcast()
    {
        if (this.broadcast == null)
        {
            IPv4Address netmask = this.getNetmask();
            IPv4Address network = this.getNetwork();
            this.broadcast = network.or(netmask.not());
        }
        return this.broadcast;
    }

    @Override
    public int compareTo(IPv4CIDR o)
    {
        return this.getAddress().equals(o.getAddress()) ?
                Integer.compare(this.maskBits, o.maskBits) :
                    this.getAddress().compareTo(o.getAddress());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + maskBits;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IPv4CIDR other = (IPv4CIDR) obj;
        if (address == null)
        {
            if (other.address != null) return false;
        }
        else if (!address.equals(other.address)) return false;
        if (maskBits != other.maskBits) return false;
        return true;
    }
    
    public String toString()
    {
        return this.address.toString() + "/" + this.maskBits;
    }

    public static IPv4CIDR fromString(String cidr)
    {
        Matcher match = IPV4_CIDR_FORMAT.matcher(Objects.requireNonNull(cidr));
        if (!match.matches()) throw new IllegalArgumentException("Invalid IPv4 CIDR");
        IPv4Address address = IPv4Address.fromString(match.group(1));
        int maskBits = Integer.parseInt(match.group(2));
        if (maskBits < 0 || maskBits > 32) throw new IllegalArgumentException("Invalid IPv4 CIDR");
        return new IPv4CIDR(address, maskBits);
    }
    
    public static IPv4CIDR fromAddressAndNetmask(IPv4Address address, IPv4Address netmask)
    {
        return new IPv4CIDR(Objects.requireNonNull(address), Objects.requireNonNull(netmask).pop());
    }
    
    public static class IPv4CIDRFromStringConverter extends StdConverter<String, IPv4CIDR>
    {
        public IPv4CIDR convert(String value)
        {
            return value == null ? null : IPv4CIDR.fromString(value);
        }
    }
}
