package com.intrbiz.virt.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

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

@SQLTable(schema = VirtDB.class, name = "network", since = @SQLVersion({ 1, 0, 0 }) )
public class Network
{   
    public static interface TYPE
    {
        public static final String VXLAN = "vxlan";
    }
    
    @SQLColumn(index = 1, name = "id", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private UUID id;

    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.CASCADE, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }) )
    private UUID accountId;
    
    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLUnique(name = "network_name_unq", columns = { "account_id", "name" })
    private String name;
    
    @SQLColumn(index = 4, name = "vxlan_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLUnique(name = "vxlan_id_unq")
    private int vxlanId;
    
    @SQLColumn(index = 5, name = "cidr", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    private String cidr;
    
    @SQLColumn(index = 6, name = "ipv4_router", since = @SQLVersion({ 1, 0, 0 }) )
    private String ipv4Router;
    
    @SQLColumn(index = 7, name = "ipv4_dns1", since = @SQLVersion({ 1, 0, 0 }) )
    private String ipv4DNS1;
    
    @SQLColumn(index = 8, name = "ipv4_dns2", since = @SQLVersion({ 1, 0, 0 }) )
    private String ipv4DNS2;
    
    @SQLColumn(index = 9, name = "description", since = @SQLVersion({ 1, 0, 0 }) )
    private String description;
    
    @SQLColumn(index = 10, name = "network_type", since = @SQLVersion({ 1, 0, 0 }) )
    private String networkType;
    
    @SQLColumn(index = 11, name = "metadata", type = "JSONB", since = @SQLVersion({ 1, 0, 0 }) )
    private String metadata;
    
    @SQLColumn(index = 12, name = "zone_id", since = @SQLVersion({ 1, 0, 6 }))
    @SQLForeignKey(references = Zone.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 6 }))
    private UUID zoneId;

    public Network()
    {
        super();
    }

    public Network(Zone zone, Account account, String name, String cidr, String description)
    {
        super();
        this.id = UUID.randomUUID();
        this.zoneId = zone.getId();
        this.accountId = account.getId();
        this.name = name;
        this.description = description;
        // this is a VXLAN network
        this.networkType = TYPE.VXLAN;
        this.vxlanId = IDUtil.randomVxlanId();
        // ensure the CIDR is aligned
        this.cidr = alignCIDR(cidr);
        // allocate the default reserved addresses
        Iterator<String> resAddr = this.getReservedAddresses().iterator();
        this.ipv4Router = resAddr.next();
        this.ipv4DNS1 = resAddr.next();
        this.ipv4DNS2 = resAddr.next();
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
    
    public String getVxlanSuffix()
    {
        return IDUtil.vxlanHex(this.vxlanId);
    }
    
    public NetworkEO toEvent()
    {
        return new NetworkEO(this.id, this.name, this.vxlanId, this.networkType);
    }
    
    public List<MachineNIC> getAllocations()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachineNICsInNetwork(this.id);
        }
    }
    
    public Zone getZone()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getZone(this.zoneId);
        }
    }
    
    /**
     * Get the list of reserved addresses within this network
     */
    public List<String> getReservedAddresses()
    {
        List<String> ret = new ArrayList<String>();
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        String[] addresses = subnet.getAllAddresses();
        for (int i = 0; i < 9; i++)
        {
            ret.add(addresses[i]);
        }
        ret.add(addresses[addresses.length - 1]);
        return ret;
    }
    
    /**
     * Choose an address at random for this network, the returned address may not be free.
     */
    public String getRandomAddress()
    {
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        String[] addresses = subnet.getAllAddresses();
        return addresses[(new SecureRandom()).nextInt(addresses.length - 10) + 9];
    }
    
    /**
     * Get the number of addresses which are usable in this network
     */
    public long getUsableAddresses()
    {
        SubnetInfo subnet = new SubnetUtils(this.cidr).getInfo();
        return subnet.getAddressCountLong() - 10;
    }
    
    // Helpers
    
    /**
     * Is the given CIDR usable as a network.  Networks need a CIDR of /24 at 
     * minimum and must be within 10.0.0.0/8
     */
    public static boolean isCIDRUsable(String cidr)
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
    public static String alignCIDR(String cidr)
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
