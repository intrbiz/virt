package com.intrbiz.virt.metadata.router;

import static com.intrbiz.virt.metadata.model.network.NameserverNetworkConfigV1.*;
import static com.intrbiz.virt.metadata.model.network.PhysicalNetworkConfigV1.*;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Before;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.metadata.Var;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.metadata.MetadataApp;
import com.intrbiz.virt.metadata.model.network.MachineMetadataV1;
import com.intrbiz.virt.metadata.model.network.NetworkV1;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.Network;

@Prefix("/metadata/")
public class MetadataRouter extends Router<MetadataApp>
{
    private static final Logger logger = Logger.getLogger(MetadataRouter.class);
    
    @Before
    @Any("/**")
    @WithDataAdapter(VirtDB.class)
    public void filterMachine(VirtDB db)
    {
        // translate the client to the machine MAC address
        String cfgMac = app().arpTable().getInstanceMAC(request().getRemoteAddress());
        require(cfgMac != null, "Cannot resolve MAC address");
        // lookup the machine
        Machine machine = db.getMachineByCfgMAC(cfgMac);
        require(machine != null, "No such machine");
        var("machine", machine);
        logger.info("Got metadata request " + request().getPathInfo() + " for machine: " + request().getRemoteAddress() + " ==> " + cfgMac + ", " + machine.getId());
    }
    
    @Any("/v1.json")
    @JSON
    public MachineMetadataV1 metadataV2(@Var("machine") Machine machine)
    {
        return new MachineMetadataV1()
                .withZone(machine.getZone().getName())
                .withMachine(machine.getId(), machine.getName())
                .withSSHKey(machine.getSSHKey().getKey())
                .withNetwork(this.network(machine));
    }
    
    @Any("/id")
    @Text
    public String id(@Var("machine") Machine machine)
    {
        return machine.getId().toString();
    }
    
    @Any("/zone")
    @Text
    public String zone(@Var("machine") Machine machine)
    {
        return machine.getZone().getName();
    }
    
    @Any("/hostname")
    @Text
    public String hostname(@Var("machine") Machine machine)
    {
        return machine.getName();
    }
    
    
    @Any("/public_keys")
    @Text
    public String publicKeys(@Var("machine") Machine machine)
    {
        return machine.getSSHKey().getKey();
    }
    
    @Any("/user_data")
    @Text
    public String userData(@Var("machine") Machine machine)
    {
        return "";
    }
    
    @Any("/network.json")
    @JSON
    public NetworkV1 network(@Var("machine") Machine machine)
    {
        NetworkV1 network = new NetworkV1();
        // eth0 - configuration nic
        network.with(physical("eth0", machine.getCfgMac()).dhcpAddress());
        // eth1..n user nics
        int i = 1;
        for (MachineNIC nic : machine.getInterfaces())
        {
            Network net = nic.getNetwork();
            if (i == 1)
            {
                // primary nic
                network.with(physical("eth" + i, nic.getMac()).staticAddress(nic.getIpv4() + "/" + net.getIPv4NetworkMaskBits(), net.getIpv4Router()));
                // nameserver for primary network
                network.with(nameserver().nameservers(net.getIpv4DNS1()).nameservers(net.getIpv4DNS2()));
            }
            else
            {
                // additional nic
                network.with(physical("eth" + i, nic.getMac()).staticAddress(nic.getIpv4() + "/" + net.getIPv4NetworkMaskBits()));
            }
            i++;
        }
        return network;
    }
    
    @Any("/account/id")
    @Text
    public String accountId(@Var("machine") Machine machine)
    {
        return machine.getAccountId().toString();
    }
    
    @Any("/account/name")
    @Text
    public String accountName(@Var("machine") Machine machine)
    {
        return machine.getAccount().getName();
    }
}
