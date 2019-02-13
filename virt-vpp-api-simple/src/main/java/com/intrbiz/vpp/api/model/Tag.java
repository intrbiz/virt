package com.intrbiz.vpp.api.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.converter.ToStringConverter;
import com.intrbiz.vpp.api.model.Tag.TagFromStringConverter;

@JsonDeserialize(converter = TagFromStringConverter.class)
@JsonSerialize(converter = ToStringConverter.class)
public class Tag implements Comparable<Tag>, Serializable
{
    private static final long serialVersionUID = 1L;
    
    private final String value;
    
    public Tag(String value)
    {
        Objects.requireNonNull(value);
        if (value.getBytes().length > 64) throw new IllegalArgumentException("The tag cannot be longer than 64 characters"); 
        this.value = value;
    }
    
    public String getValue()
    {
        return this.value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Tag other = (Tag) obj;
        if (value == null)
        {
            if (other.value != null) return false;
        }
        else if (!value.equals(other.value)) return false;
        return true;
    }

    @Override
    public int compareTo(Tag o)
    {
        return this.value.compareTo(o.value);
    }
    
    public String toString()
    {
        return this.value;
    }
    
    public static final Tag fromString(String tag) {
        return tag == null ? null : new Tag(tag);
    }
    
    public static final Tag getVMInterfaceTag(MACAddress vmMACAddress)
    {
        return new Tag("vm-" + vmMACAddress.toCompactString());
    }
    
    public static class TagFromStringConverter extends StdConverter<String, Tag>
    {
        public Tag convert(String value)
        {
            return value == null ? null : new Tag(value);
        }
    }
}
