package com.intrbiz.vpp.api.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

public class ToStringConverter extends StdConverter<Object, String>
{
    @Override
    public String convert(Object value)
    {
        return value == null ? null : value.toString();
    }
}
