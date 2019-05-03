package com.intrbiz.virt.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.data.db.compiler.meta.Action;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLForeignKey;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.event.model.NetworkEO;
import com.intrbiz.virt.util.IDUtil;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonTypeName("network")
@SQLTable(schema = VirtDB.class, name = "network", since = @SQLVersion({ 1, 0, 0 }) )
public class Network
{   
    public static interface TYPE
    {
        public static final String VXLAN = "vxlan";
    }
    
    @JsonProperty("id")
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private UUID id;

    @JsonProperty("account_id")
    @SQLColumn(index = 2, name = "account_id", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.CASCADE, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }) )
    private UUID accountId;
    
    @JsonProperty("name")
    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLUnique(name = "network_name_unq", columns = { "account_id", "name" })
    private String name;
    
    @JsonIgnore
    @SQLColumn(index = 4, name = "vxlan_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLUnique(name = "vxlan_id_unq")
    private int vxlanId;
    
    @JsonProperty("cidr")
    @SQLColumn(index = 5, name = "cidr", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    private String cidr;
    
    @JsonProperty("router")
    @SQLColumn(index = 6, name = "ipv4_router", since = @SQLVersion({ 1, 0, 0 }) )
    private String ipv4Router;
    
    @JsonProperty("dns1")
    @SQLColumn(index = 7, name = "ipv4_dns1", since = @SQLVersion({ 1, 0, 0 }) )
    private String ipv4DNS1;
    
    @JsonProperty("dns2")
    @SQLColumn(index = 8, name = "ipv4_dns2", since = @SQLVersion({ 1, 0, 0 }) )
    private String ipv4DNS2;
    
    @JsonProperty("description")
    @SQLColumn(index = 9, name = "description", since = @SQLVersion({ 1, 0, 0 }) )
    private String description;
    
    @JsonIgnore
    @SQLColumn(index = 10, name = "network_type", since = @SQLVersion({ 1, 0, 0 }) )
    private String networkType;
    
    @JsonIgnore
    @SQLColumn(index = 11, name = "metadata", type = "JSONB", since = @SQLVersion({ 1, 0, 0 }) )
    private String metadata;
    
    @JsonIgnore
    @SQLColumn(index = 12, name = "zone_id", since = @SQLVersion({ 1, 0, 6 }))
    @SQLForeignKey(references = Zone.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 6 }))
    private UUID zoneId;
    
    @JsonProperty("purpose")
    @SQLColumn(index = 13, name = "purpose", since = @SQLVersion({ 1, 0, 19 }) )
    private String purpose;

    public Network()
    {
        super();
    }

    public Network(Zone zone, Account account, String name, String cidr, int vaxlanId, String description)
    {
        super();
        this.id = account != null ? account.randomObjectId() : Account.randomId(Account.NULL_UUID);
        if (zone != null) this.zoneId = zone.getId();
        if (account != null) this.accountId = account.getId();
        this.name = name;
        this.description = description;
        // this is a VXLAN network
        this.networkType = TYPE.VXLAN;
        this.vxlanId = vaxlanId;
        // ensure the CIDR is aligned
        this.cidr = alignIPv4CIDR(cidr);
        // allocate the default reserved addresses
        Iterator<String> resAddr = this.getIPv4ReservedAddresses().iterator();
        this.ipv4Router = resAddr.next();
        this.ipv4DNS1 = resAddr.next();
        this.ipv4DNS2 = resAddr.next();
    }
    
    public Network(Zone zone, Account account, String name, String cidr, String description)
    {
        this(zone, account, name, cidr, IDUtil.randomVxlanId(), description);
    }

    public UUID getZoneId()
    {
        return zoneId;
    }

    public void setZoneId(UUID zoneId)
    {
        this.zoneId = zoneId;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getVxlanId()
    {
        return vxlanId;
    }

    public void setVxlanId(int vxlanId)
    {
        this.vxlanId = vxlanId;
    }

    public String getCidr()
    {
        return cidr;
    }

    public void setCidr(String cidr)
    {
        this.cidr = cidr;
    }

    public String getIpv4Router()
    {
        return ipv4Router;
    }

    public void setIpv4Router(String ipv4Router)
    {
        this.ipv4Router = ipv4Router;
    }

    public String getIpv4DNS1()
    {
        return ipv4DNS1;
    }

    public void setIpv4DNS1(String ipv4dns1)
    {
        ipv4DNS1 = ipv4dns1;
    }

    public String getIpv4DNS2()
    {
        return ipv4DNS2;
    }

    public void setIpv4DNS2(String ipv4dns2)
    {
        ipv4DNS2 = ipv4dns2;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getNetworkType()
    {
        return networkType;
    }

    public void setNetworkType(String networkType)
    {
        this.networkType = networkType;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }
    
    public String getPurpose()
    {
        return purpose;
    }

    public void setPurpose(String purpose)
    {
        this.purpose = purpose;
    }

    @JsonIgnore
    public String getVxlanSuffix()
    {
        return IDUtil.vxlanHex(this.vxlanId);
    }
    
    public NetworkEO toEvent()
    {
        return new NetworkEO(this.id, this.name, this.vxlanId, this.networkType, this.purpose);
    }
    
    @JsonIgnore
    public List<MachineNIC> getAllocations()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineNICsInNetwork(this.id);
        }
    }
    
    @JsonProperty("zone")
    public Zone getZone()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getZone(this.zoneId);
        }
    }
    
    /**
     * Get the network address of this network
     */
    @JsonProperty("network_address")
    public String getIPv4NetworkAddress()
    {
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        return subnet.getNetworkAddress();
    }
    
    /**
     * Get the number of bits in the network mask
     */
    @JsonProperty("network_mask_bits")
    public int getIPv4NetworkMaskBits()
    {
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        return pop(subnet.asInteger(subnet.getNetmask()));
    }
    
    @JsonProperty("network_reserved_address_count")
    public int getIPv4ReservedAddressCount()
    {
        return this.getIPv4ReservedAddressCount(new SubnetUtils(this.cidr).getInfo());
    }
    
    private int getIPv4ReservedAddressCount(SubnetInfo subnet)
    {
        // Reserve 5% (>= 10 and <= 128) of the address space for critical devices, the first 9 to 127 and last address
        return Math.min(Math.max((int) Math.ceil(subnet.getAddressCountLong() * 0.05F), 9), 127) + 1;
    }
    
    /**
     * Get the list of reserved addresses within this network
     */
    //@JsonProperty("network_reserved_addresses")
    @JsonIgnore
    public List<String> getIPv4ReservedAddresses()
    {
        List<String> ret = new ArrayList<String>();
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        int reservedCount = getIPv4ReservedAddressCount(subnet);
        String[] addresses = subnet.getAllAddresses();
        for (int i = 0; i < reservedCount; i++)
        {
            ret.add(addresses[i]);
        }
        ret.add(addresses[addresses.length - 1]);
        return ret;
    }
    
    /**
     * Choose an address at random for this network, the returned address may not be free.
     */
    @JsonIgnore
    public String getIPv4RandomAddress()
    {
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        int reservedCount = getIPv4ReservedAddressCount(subnet);
        String[] addresses = subnet.getAllAddresses();
        return addresses[(new SecureRandom()).nextInt(addresses.length - reservedCount) + (reservedCount - 1)];
    }
    
    /**
     * Get the number of addresses which are usable in this network
     */
    @JsonProperty("usable_addresses")
    public long getIPv4UsableAddresses()
    {
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        return subnet.getAddressCountLong() - getIPv4ReservedAddressCount(subnet);
    }
    
    // Helpers
    
    /**
     * Is the given CIDR usable as a network.  Networks need a CIDR of /24 at 
     * minimum and must be within 10.0.0.0/8
     */
    public static boolean isIPv4CIDRUsable(String cidr)
    {
        if (cidr == null || (! cidr.startsWith("10."))) return false;
        SubnetInfo subnet = new SubnetUtils(cidr).getInfo();
        return subnet.getAddressCountLong() >= 250L;
    }
    
    /**
     * Ensure the given CIDR is aligned correctly to the network address
     * @param cidr a CIDR
     * @return an aligned CIDR
     */
    public static String alignIPv4CIDR(String cidr)
    {
        SubnetInfo subnet = new SubnetUtils(cidr).getInfo();
        return subnet.getNetworkAddress() + "/" + pop(subnet.asInteger(subnet.getNetmask()));
    }
    
    /*
     * Count the number of 1-bits in a 32-bit integer using a divide-and-conquer strategy
     * see Hacker's Delight section 5.1
     */
    private static int pop(int x) {
        x = x - ((x >>> 1) & 0x55555555);
        x = (x & 0x33333333) + ((x >>> 2) & 0x33333333);
        x = (x + (x >>> 4)) & 0x0F0F0F0F;
        x = x + (x >>> 8);
        x = x + (x >>> 16);
        return x & 0x0000003F;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Network other = (Network) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }
}
