package com.intrbiz.virt.libvirt.util;

public class Util
{
    public final static <T extends Enum<T>> T valueOf(Class<T> enumType, int ordinal)
    {
        for (T value : enumType.getEnumConstants())
        {
            if (value.ordinal() == ordinal)
            {
                return value;
            }
        }
        return null;
    }
}
