package com.intrbiz.vpp.util;

public class HexUtil
{
    private static final String HEX_CHARS = "0123456789abcdef";
    
    public static final String u24Hex(int val)
    {
        return u24Hex("", val);
    }
    
    public static final String u24Hex(String prefix, int val)
    {
        return  prefix +
                HEX_CHARS.charAt((val >> 20) & 0xF) +
                HEX_CHARS.charAt((val >> 16) & 0xF) +
                HEX_CHARS.charAt((val >> 12) & 0xF) +
                HEX_CHARS.charAt((val >> 8) & 0xF)  +
                HEX_CHARS.charAt((val >> 4) & 0xF)  +
                HEX_CHARS.charAt(val & 0xF);
    }
    
    public static final String u32Hex(int val)
    {
        return u32Hex("", val);
    }
    
    public static final String u32Hex(String prefix, int val)
    {
        return  prefix +
                HEX_CHARS.charAt((val >> 28) & 0xF) +
                HEX_CHARS.charAt((val >> 24) & 0xF) +
                HEX_CHARS.charAt((val >> 20) & 0xF) +
                HEX_CHARS.charAt((val >> 16) & 0xF) +
                HEX_CHARS.charAt((val >> 12) & 0xF) +
                HEX_CHARS.charAt((val >> 8) & 0xF)  +
                HEX_CHARS.charAt((val >> 4) & 0xF)  +
                HEX_CHARS.charAt(val & 0xF);
    }
}
