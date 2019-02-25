package com.intrbiz.vpp.api.model;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToStringConverter;
import com.intrbiz.vpp.api.model.MACAddress.MACAddressFromStringConverter;

@JsonDeserialize(converter = MACAddressFromStringConverter.class)
@JsonSerialize(converter = ToStringConverter.class)
public final class MACAddress implements Comparable<MACAddress>, Serializable
{
    private static final long serialVersionUID = 1L;

    private static final String HEX_CHARS = "0123456789abcdef";

    private static final char MAC_SEPARATOR = ':';
    
    private static final char NO_SEPARATOR = '\0';
    
    private static final byte[] MAC_BROADCAST = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
    
    public static final int MAC_LENGTH = 6;
    
    private static final MACAddress BROADCAST = new MACAddress(MAC_BROADCAST);

    private final byte[] value;

    public MACAddress(byte[] value)
    {
        super();
        Objects.requireNonNull(value);
        if (value.length < MAC_LENGTH) throw new IllegalArgumentException("MAC address must contain at least" + MAC_LENGTH + " bytes");
        this.value = new byte[MAC_LENGTH];
        System.arraycopy(value, 0, this.value, 0, this.value.length);
    }
    
    public byte[] getValue()
    {
        return this.value;
    }
    
    public MACAddress setLocallyAdministered(boolean value)
    {
        byte[] cloned = new byte[this.value.length];
        System.arraycopy(this.value, 0, cloned, 0, cloned.length);
        cloned[0] = (byte) ((cloned[0] & 0xFD) | (value ? 0x02 : 0x00));
        return new MACAddress(cloned);
    }
    
    public boolean isLocallyAdministered()
    {
        return (this.value[0] & 0x02) == 0x02;
    }
    
    public MACAddress setMulticast(boolean value)
    {
        byte[] cloned = new byte[this.value.length];
        System.arraycopy(this.value, 0, cloned, 0, cloned.length);
        cloned[0] = (byte) ((cloned[0] & 0xFE) | (value ? 0x01 : 0x00));
        return new MACAddress(cloned);
    }
    
    public boolean isMulticast()
    {
        return (this.value[0] & 0x01) == 0x01;
    }
    
    public boolean isBroadcast()
    {
        return Arrays.equals(this.value, MAC_BROADCAST);
    }
    
    public byte[] getOrganizationallyUniqueIdentifier()
    {
        return this.isLocallyAdministered() ? null : 
            new byte[] { this.value[0], this.value[1], this.value[2] };
    }

    @Override
    public int compareTo(MACAddress o)
    {
        return this.toString().compareTo(o.toString());
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
        MACAddress other = (MACAddress) obj;
        if (!Arrays.equals(value, other.value)) return false;
        return true;
    }
    
    public String toString()
    {
        return toString(MAC_SEPARATOR);
    }
    
    public String toCompactString()
    {
        return toString(NO_SEPARATOR);
    }

    public String toString(char separator)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.value.length; i++)
        {
            if (i > 0 && separator > 0) sb.append(separator);
            sb.append(HEX_CHARS.charAt((this.value[i] >> 4) & 0xF));
            sb.append(HEX_CHARS.charAt(this.value[i] & 0xF));
        }
        return sb.toString();
    }
    
    public static MACAddress random()
    {
        byte[] mac = new byte[MAC_LENGTH];
        new SecureRandom().nextBytes(mac);
        // mac[0] = (byte) ((mac[0] & 0xFC) | 0x02);
        mac[0] = 0x52;
        mac[1] = 0x54;
        mac[2] = 0x01;
        return new MACAddress(mac);
    }
    
    public static MACAddress broadcast()
    {
        return BROADCAST;
    }
    
    public static MACAddress fromString(String mac)
    {
        String[] parts = mac.split("[:.-]");
        if (parts.length != 6) throw new IllegalArgumentException("MAC address must have 6 octets");
        byte[] bytes = new byte[6];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return new MACAddress(bytes);
    }
    
    public static class MACAddressFromStringConverter extends StdConverter<String, MACAddress>
    {
        public MACAddress convert(String value)
        {
            return value == null ? null : MACAddress.fromString(value);
        }
    }
}
