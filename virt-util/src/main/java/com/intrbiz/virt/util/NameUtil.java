package com.intrbiz.virt.util;

public class NameUtil
{
    public static String toSafeName(String in)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < in.toLowerCase().length(); i++)
        {
            char c = in.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))
                sb.append(c);
            else if (c == '-' || c == ' ' || c == '_' || c == '/' || c == '\\' || c == ':')
                sb.append('-');
        }
        return sb.toString();
    }
}
