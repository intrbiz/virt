package com.intrbiz.vpp.util;

public class JVPPUtil
{   
    public static final byte TRUE = 1;
    
    public static final byte FALSE = 0;
    
    public static final byte convertBoolean(boolean b)
    {
        return b ? (byte) TRUE : (byte) FALSE;
    }
    
    public static final boolean convertBoolean(byte b)
    {
        return b != FALSE;
    }
    
    public static final byte[] convertString(String s)
    {
        if (s == null) return new byte[] { 0x00 };
        byte[] unterminated = s.getBytes();
        byte[] terminated = new byte[unterminated.length + 1];
        System.arraycopy(unterminated, 0, terminated, 0, unterminated.length);
        return terminated;
    }
    
    public static final String convertString(byte[] b)
    {
        if (b == null)
            return null;
        return new String(b, 0, strLen(b));
    }
    
    public static final int strLen(byte[] b)
    {
        int end = 0;
        while (end < b.length)
        {
            if (b[end] == 0) break;
            end++;
        }
        return end;
    }
}
