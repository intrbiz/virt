package com.intrbiz.virt.metadata.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class ARPTable
{   
    public static final String DEFAULT_METADATA_BRIDGE = "brcfg";
    
    private final String metadataBridge; 
    
    public ARPTable(String metadataBridge)
    {
        this.metadataBridge = metadataBridge;
    }
    
    public ARPTable()
    {
        this(DEFAULT_METADATA_BRIDGE);
    }
    
    public String getMetadataBridge()
    {
        return this.metadataBridge;
    }
    
    public String getInstanceMAC(String ip)
    {
        return this.readARPTable().stream()
                .filter((e) -> this.metadataBridge.equals(e.getDevice()))
                .filter((e) -> ip.equals(e.getIp()))
                .findFirst()
                .map((e) -> e.getHwAddress())
                .orElse(null);
    }
    
    public List<ARPTableEntry> readARPTable()
    {
        List<ARPTableEntry> table = new LinkedList<ARPTableEntry>();
        boolean header = false;
        try
        {
            try (BufferedReader in = new BufferedReader(new FileReader("/proc/net/arp")))
            {
                String line;
                while ((line = in.readLine()) != null)
                {
                    if (header)
                    {
                        table.add(new ARPTableEntry(line));
                    }
                    else
                    {
                        header = true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to read ARP table", e);
        }
        return table;
    }
    
    public static class ARPTableEntry
    {
        private final String ip;
        private final String hwType;
        private final String flags;
        private final String hwAddress;
        private final String mask;
        private final String device;
        
        public ARPTableEntry(String line)
        {
            String[] parts = line.split(" +");
            this.ip        = parts[0].trim();
            this.hwType    = parts[1].trim();
            this.flags     = parts[2].trim();
            this.hwAddress = parts[3].trim();
            this.mask      = parts[4].trim();
            this.device    = parts[5].trim();
        }

        public String getIp()
        {
            return ip;
        }

        public String getHwType()
        {
            return hwType;
        }

        public String getFlags()
        {
            return flags;
        }

        public String getHwAddress()
        {
            return hwAddress;
        }

        public String getMask()
        {
            return mask;
        }

        public String getDevice()
        {
            return device;
        }
        
        public String toString()
        {
            return this.ip + " => " + this.hwAddress;
        }
    }
}
