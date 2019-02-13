package com.intrbiz.vpp.api.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.util.IntValue;

public class ToIntConverter extends StdConverter<IntValue, Integer>
{
    @Override
    public Integer convert(IntValue value)
    {
        return value == null ? null : value.getValue();
    }
}
