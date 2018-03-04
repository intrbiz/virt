package com.intrbiz.virt.model;

import java.util.UUID;

import com.intrbiz.data.db.compiler.meta.Action;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLForeignKey;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLUnique;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.event.model.MachineInterfaceEO;
import com.intrbiz.virt.util.IDUtil;

@SQLTable(schema = VirtDB.class, name = "machine_nic", since = @SQLVersion({ 1, 0, 0 }) )
public class MachineNIC
{
    @SQLColumn(index = 1, name = "machine_id", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLForeignKey(references = Machine.class, on = "id", onDelete = Action.CASCADE, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private UUID machineId;
    
    @SQLColumn(index = 2, name = "network_id", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLForeignKey(references = Network.class, on = "id", onDelete = Action.RESTRICT, onUpdate = Action.RESTRICT, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private UUID networkId;
    
    @SQLColumn(index = 3, name = "name", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    private String name;
    
    @SQLColumn(index = 4, name = "mac", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLUnique(name = "network_mac_unq", columns = { "network_id", "mac" })
    private String mac;
    
    @SQLColumn(index = 5, name = "ipv4", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLUnique(name = "network_ipv4_unq", columns = { "network_id", "ipv4" })
    private String ipv4;

    public MachineNIC()
    {
        super();
    }

    public MachineNIC(Machine machine, String name, Network network)
    {
        super();
        this.machineId = machine.getId();
        this.networkId = network.getId();
        this.name = name;
        this.mac = IDUtil.formatMac(IDUtil.randomMac());
        this.ipv4 = network.getRandomAddress();
    }



    public UUID getMachineId()
    {
        return machineId;
    }

    public void setMachineId(UUID machineId)
    {
        this.machineId = machineId;
    }

    public UUID getNetworkId()
    {
        return networkId;
    }

    public void setNetworkId(UUID networkId)
    {
        this.networkId = networkId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMac()
    {
        return mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public String getIpv4()
    {
        return ipv4;
    }

    public void setIpv4(String ipv4)
    {
        this.ipv4 = ipv4;
    }
    
    public Machine getMachine()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getMachine(this.machineId);
        }
    }
    
    public Network getNetwork()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getNetwork(this.networkId);
        }
    }
    
    public MachineInterfaceEO toEvent()
    {
        return new MachineInterfaceEO(this.name, this.mac, this.getNetwork().toEvent());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((machineId == null) ? 0 : machineId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MachineNIC other = (MachineNIC) obj;
        if (machineId == null)
        {
            if (other.machineId != null) return false;
        }
        else if (!machineId.equals(other.machineId)) return false;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }
}
