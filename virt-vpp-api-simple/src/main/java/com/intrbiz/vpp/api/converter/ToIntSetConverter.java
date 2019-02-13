package com.intrbiz.vpp.api.converter;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.intrbiz.vpp.api.util.IntValue;

public class ToIntSetConverter extends StdConverter<Set<? extends IntValue>, Set<Integer>>
{
    @Override
    public Set<Integer> convert(Set<? extends IntValue> value)
    {
        return value == null ? null : value.stream().map(IntValue::getValue).collect(Collectors.toSet());
    }
}
