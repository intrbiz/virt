package com.intrbiz.virt.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.intrbiz.Util;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@SQLTable(schema = VirtDB.class, name = "machine_type", since = @SQLVersion({ 1, 0, 0 }))
public class MachineType
{
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    private UUID id;

    @SQLColumn(index = 2, name = "family", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private String family;

    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    @SQLUnique(name = "family_name_unq", columns = { "family", "name" })
    private String name;

    @SQLColumn(index = 4, name = "cpus", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private int cpus;

    @SQLColumn(index = 5, name = "memory", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private long memory;

    @SQLColumn(index = 6, name = "volume_limit", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private int volumeLimit;

    @SQLColumn(index = 7, name = "supported_volume_types", type = "TEXT[]", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private List<String> supportedVolumeTypes = new LinkedList<String>();

    @SQLColumn(index = 8, name = "nic_limit", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private int nicLimit;

    @SQLColumn(index = 9, name = "supported_network_types", type = "TEXT[]", notNull = true, since = @SQLVersion({ 1, 0, 0 }))
    private List<String> supportedNetworkTypes = new LinkedList<String>();

    @SQLColumn(index = 10, name = "ephemeral_volumes", type = "TEXT[]", since = @SQLVersion({ 1, 0, 8 }))
    private List<String> ephemeralVolumes = new LinkedList<String>();

    public MachineType()
    {
        super();
    }

    public MachineType(String family, String name)
    {
        super();
        this.id = UUID.randomUUID();
        this.family = family;
        this.name = name;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getCpus()
    {
        return cpus;
    }

    public void setCpus(int cpus)
    {
        this.cpus = cpus;
    }

    public long getMemory()
    {
        return memory;
    }

    public void setMemory(long memory)
    {
        this.memory = memory;
    }

    public String getFamily()
    {
        return family;
    }

    public void setFamily(String family)
    {
        this.family = family;
    }

    public int getVolumeLimit()
    {
        return volumeLimit;
    }

    public void setVolumeLimit(int volumeLimit)
    {
        this.volumeLimit = volumeLimit;
    }

    public List<String> getSupportedVolumeTypes()
    {
        return supportedVolumeTypes;
    }

    public void setSupportedVolumeTypes(List<String> supportedVolumeTypes)
    {
        this.supportedVolumeTypes = supportedVolumeTypes;
    }

    public int getNicLimit()
    {
        return nicLimit;
    }

    public void setNicLimit(int nicLimit)
    {
        this.nicLimit = nicLimit;
    }

    public List<String> getSupportedNetworkTypes()
    {
        return supportedNetworkTypes;
    }

    public void setSupportedNetworkTypes(List<String> supportedNetworkTypes)
    {
        this.supportedNetworkTypes = supportedNetworkTypes;
    }

    public List<String> getEphemeralVolumes()
    {
        return ephemeralVolumes;
    }

    public void setEphemeralVolumes(List<String> ephemeralVolumes)
    {
        this.ephemeralVolumes = ephemeralVolumes;
    }
    
    public List<EphemeralVolume> parseEphemeralVolumes()
    {
        if (this.ephemeralVolumes == null) return new LinkedList<EphemeralVolume>();
        return this.ephemeralVolumes.stream()
                .map((s) -> new EphemeralVolume(s))
                .collect(Collectors.toList());
    }
    
    public int getEphemeralVolumeCount()
    {
        return this.ephemeralVolumes == null ? 0 : this.ephemeralVolumes.size();
    }
    
    public int getVolumeCount()
    {
        return this.getEphemeralVolumeCount() + 1;
    }

    public static class EphemeralVolume
    {
        private static final Pattern DECODER = Pattern.compile("\\A([a-zA-Z0-9_-]+)\\((.*)\\):([0-9]+[MGT])\\z");

        private final String volumeType;

        private final Map<String, String> volumeTypeMetadata = new HashMap<String, String>();

        private final long size;

        public EphemeralVolume(String encoded)
        {
            super();
            Matcher match = DECODER.matcher(encoded);
            if (match.matches())
            {
                this.volumeType = match.group(1);
                parseVolumeTypeMetadata(match.group(2), this.volumeTypeMetadata);
                this.size = parseVolumeSize(match.group(3));
            }
            else
            {
                throw new IllegalArgumentException("Invalid ephemeral volume string");
            }
        }

        public EphemeralVolume(String volumeType, Map<String, String> volumeTypeMetadata, long size)
        {
            super();
            this.volumeType = volumeType;
            this.volumeTypeMetadata.putAll(volumeTypeMetadata);
            this.size = size;
        }

        public String getVolumeType()
        {
            return volumeType;
        }

        public Map<String, String> getVolumeTypeMetadata()
        {
            return volumeTypeMetadata;
        }

        public long getSize()
        {
            return this.size;
        }
        
        public String toVolumeTypeMetadataString()
        {
            return this.volumeTypeMetadata.entrySet().stream().map((e) -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
        }
        
        public String toSizeString()
        {
            return (this.size / 1_000_000L) + "M";
        }

        public String toString()
        {
            return this.volumeType + "(" + this.toVolumeTypeMetadataString() + "):" + this.toSizeString();
        }

        private static void parseVolumeTypeMetadata(String metadata, Map<String, String> volumeTypeMetadata)
        {
            for (String param : metadata.split(","))
            {
                if (!Util.isEmpty(param))
                {
                    int eq = param.indexOf("=");
                    if (eq > 0)
                        volumeTypeMetadata.put(param.substring(0, eq).trim(), param.substring(eq + 1).trim());
                    else
                        volumeTypeMetadata.put(param, "true");
                }
            }
        }

        private static long parseVolumeSize(String size)
        {
            char multiplier = size.charAt(size.length() - 1);
            long value = Long.parseLong(size.substring(0, size.length() - 1));
            switch (multiplier)
            {
                case 'T':
                    return value * 1_000_000_000_000L;
                case 'G':
                    return value * 1_000_000_000L;
                default:
                    return value * 1_000_000L;
            }
        }
        
        public static List<EphemeralVolume> parse(String volumes)
        {
            List<EphemeralVolume> vols = new LinkedList<EphemeralVolume>();
            if (! Util.isEmpty(volumes))
            {
                for (String line : volumes.split("(\\r\\n)|(\\r)|(\\n)"))
                {
                    if (! Util.isEmpty(line))
                        vols.add(new EphemeralVolume(line));
                }
            }
            return vols;
        }
    }
}
