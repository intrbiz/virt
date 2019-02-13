package com.intrbiz.vpp.api.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.util.ShortValue;

public class ToShortConverter extends StdConverter<ShortValue, Short>
{
    @Override
    public Short convert(ShortValue value)
    {
        return value == null ? null : value.getValue();
    }
}
