package com.intrbiz.vpp.api.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.util.ByteValue;

public class ToByteConverter extends StdConverter<ByteValue, Byte>
{
    @Override
    public Byte convert(ByteValue value)
    {
        return value == null ? null : value.getValue();
    }
}
