package com.intrbiz.virt.metadata.router;

import static com.intrbiz.virt.metadata.model.NameserverNetworkConfigV1.*;
import static com.intrbiz.virt.metadata.model.PhysicalNetworkConfigV1.*;

import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.metadata.Var;
import com.intrbiz.virt.VirtHostApp;
import com.intrbiz.virt.metadata.model.MachineMetadataNoCloud;
import com.intrbiz.virt.metadata.model.MachineMetadataV1;
import com.intrbiz.virt.metadata.model.NetworkV1;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.Network;

@Prefix("/")
public class GuestMetadataRouter extends Router<VirtHostApp>
{
    
    @Any("/meta-data")
    @JSON
    public MachineMetadataNoCloud metadataNoCloud(@Var("machine") Machine machine)
    {
        return new MachineMetadataNoCloud()
                .withMachine(machine.getId(), machine.getName())
                .withSSHKey(machine.getSSHKey().getAllKeys());
    }
    
    @Any("/metadata/v1.json")
    @JSON
    public MachineMetadataV1 metadataV1(@Var("machine") Machine machine)
    {
        return new MachineMetadataV1()
                .withZone(machine.getZone().getName())
                .withMachine(machine.getId(), machine.getName())
                .withSSHKey(machine.getSSHKey().getAllKeys())
                .withNetwork(this.network(machine));
    }
    
    @Any(value="(?:user-data|metadata/user_data)", regex=true)
    @Text
    public String userData(@Var("machine") Machine machine)
    {
        // Our User Data is always in cloud-config format
        StringBuilder userData = new StringBuilder("#cloud-config\n");
        userData.append("debug: True\n");
        // SSH Keys
        userData.append("disable_root: False\n");
        userData.append("ssh_deletekeys: True\n");
        userData.append("ssh_pwauth: False\n");
        userData.append("ssh_authorized_keys:\n");
        for (String key : machine.getSSHKey().getAllKeys())
        {
            userData.append("  - ").append(key.replace('\n', ' ').replace('\n', ' ')).append("\n");
        }
        // Phone Home
        userData.append("phone_home:\n");
        userData.append("  url: http://172.16.169.254:8888/status/boot\n");
        userData.append("  post: [ pub_key_dsa, pub_key_rsa, pub_key_ecdsa, instance_id ]\n");
        // Append custom user data
        if (machine.getUserData() != null) userData.append(machine.getUserData());
        return userData.toString();
    }
    
    @Any(value="(?:network-config|metadata/network.json)", regex=true)
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
    
    @Any("/metadata/id")
    @Text
    public String id(@Var("machine") Machine machine)
    {
        return machine.getId().toString();
    }
    
    @Any("/metadata/zone")
    @Text
    public String zone(@Var("machine") Machine machine)
    {
        return machine.getZone().getName();
    }
    
    @Any("/metadata/name")
    @Text
    public String name(@Var("machine") Machine machine)
    {
        return machine.getName();
    }
    
    @Any("/metadata/hostname")
    @Text
    public String hostname(@Var("machine") Machine machine)
    {
        return machine.getName();
    }
    
    @Any("/metadata/cfg_mac")
    @Text
    public String cfgMac(@Var("machine") Machine machine)
    {
        return machine.getCfgMac();
    }
    
    @Any(value="metadata/public[_-]keys", regex=true)
    @Text
    public String publicKeys(@Var("machine") Machine machine)
    {
        return machine.getSSHKey().getAllKeys().stream().collect(Collectors.joining("\n"));
    }
    
    @Any("/metadata/type/name")
    @Text
    public String machineTypeName(@Var("machine") Machine machine)
    {
        return machine.getType().getName();
    }
    
    @Any("/metadata/type/family")
    @Text
    public String machineTypeFamily(@Var("machine") Machine machine)
    {
        return machine.getType().getFamily();
    }
    
    @Any("/metadata/type/cpus")
    @Text
    public String machineTypeCpus(@Var("machine") Machine machine)
    {
        return Integer.toString(machine.getType().getCpus());
    }
    
    @Any("/metadata/type/memory")
    @Text
    public String machineTypeMemory(@Var("machine") Machine machine)
    {
        return Long.toString(machine.getType().getMemory());
    }
    
    @Any("/metadata/type/volume_limit")
    @Text
    public String machineTypeVolumeLimit(@Var("machine") Machine machine)
    {
        return Integer.toString(machine.getType().getVolumeLimit());
    }
    
    @Any("/metadata/type/volume_count")
    @Text
    public String machineTypeVolumeCount(@Var("machine") Machine machine)
    {
        return Integer.toString(machine.getType().getVolumeCount());
    }
    
    @Any("/metadata/type/ephemeral_volume_count")
    @Text
    public String machineTypeEphemeralVolumeCount(@Var("machine") Machine machine)
    {
        return Integer.toString(machine.getType().getEphemeralVolumeCount());
    }
    
    @Any("/metadata/type/ephemeral_volumes")
    @Text
    public String machineTypeEphemeralVolumes(@Var("machine") Machine machine)
    {
        return machine.getType().getEphemeralVolumes().stream().collect(Collectors.joining("\n"));
    }
    
    @Any("/metadata/type/nic_limit")
    @Text
    public String machineTypeNicLimit(@Var("machine") Machine machine)
    {
        return Integer.toString(machine.getType().getNicLimit());
    }
    
    @Any("/metadata/image/name")
    @Text
    public String machineImageName(@Var("machine") Machine machine)
    {
        return machine.getImage().getName();
    }
    
    @Any("/metadata/image/provider")
    @Text
    public String machineImageProvider(@Var("machine") Machine machine)
    {
        return machine.getImage().getProvider();
    }
    
    @Any("/metadata/image/vendor")
    @Text
    public String machineImageVendor(@Var("machine") Machine machine)
    {
        return machine.getImage().getVendor();
    }
    
    @Any("/metadata/image/product")
    @Text
    public String machineImageProduct(@Var("machine") Machine machine)
    {
        return machine.getImage().getProduct();
    }
    
    @Any("/metadata/image/size")
    @Text
    public String machineImageSize(@Var("machine") Machine machine)
    {
        return Long.toString(machine.getImage().getSize());
    }
    
    @Any("/metadata/image/open")
    @Text
    public String machineImageOpen(@Var("machine") Machine machine)
    {
        return Boolean.toString(machine.getImage().isOpen());
    }
    
    @Any("/metadata/account/id")
    @Text
    public String accountId(@Var("machine") Machine machine)
    {
        return machine.getAccountId().toString();
    }
    
    @Any("/metadata/account/name")
    @Text
    public String accountName(@Var("machine") Machine machine)
    {
        return machine.getAccount().getName();
    }
}
