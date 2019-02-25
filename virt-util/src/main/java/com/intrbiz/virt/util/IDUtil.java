package com.intrbiz.virt.util;

import java.security.SecureRandom;

public class IDUtil
{
    private static final String HEX_CHARS = "0123456789abcdef";
    
    /**
     * Generate a random OUI locally administered MAC address
     * @return a 6 byte MAC address
     */
    public static byte[] randomMac()
    {
        byte[] mac = new byte[6];
        new SecureRandom().nextBytes(mac);
        //mac[0] = (byte) ((mac[0] & 0xFC) | 0x02);
        mac[0] = 0x52;
        mac[1] = 0x54;
        mac[2] = 0x00;
        return mac;
    }
    
    /**
     * Format the given MAC in standard notation
     * @param mac the MAC to format
     * @return a colon separated hex formatted MAC address
     */
    public static final String formatMac(byte[] mac)
    {
        return formatMac(mac, ':');
    }
    
    /**
     * Format the given MAC with no separator
     * @param mac the mac to format
     */
    public static final String formatMacShort(byte[] mac)
    {
        return formatMac(mac, '\0');
    }
    
    /**
     * Format a MAC in the normal hex notation
     * @param mac the mac to format
     * @param separator optional byte separator
     */
    public static final String formatMac(byte[] mac, char separator)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++)
        {
            if (i > 0 && separator != '\0') sb.append(separator);
            sb.append(HEX_CHARS.charAt((mac[i] >> 4) & 0xF));
            sb.append(HEX_CHARS.charAt(mac[i] & 0xF));
        }
        return sb.toString();
    }
    
    /**
     * Reliably convert vxlan ids to Linux interface names
     * @param prefix the interface prefix
     * @param vxlanId the vxlan id
     * @return the interface name
     */    
    public static String vxlanHex(int vxlanId)
    {
        return vxlanHex("", vxlanId);
    }
    
    public static String vxlanHex(String prefix, int vxlanId)
    {
        return  prefix +
                HEX_CHARS.charAt((vxlanId >> 20) & 0xF) +
                HEX_CHARS.charAt((vxlanId >> 16) & 0xF) +
                HEX_CHARS.charAt((vxlanId >> 12) & 0xF) +
                HEX_CHARS.charAt((vxlanId >> 8) & 0xF)  +
                HEX_CHARS.charAt((vxlanId >> 4) & 0xF)  +
                HEX_CHARS.charAt(vxlanId & 0xF);
    }
    
    /**
     * Generate a random 24bit VXLAN ID
     */
    public static int randomVxlanId()
    {
        return new SecureRandom().nextInt() & 0x00FFFFFF;
    }
}
