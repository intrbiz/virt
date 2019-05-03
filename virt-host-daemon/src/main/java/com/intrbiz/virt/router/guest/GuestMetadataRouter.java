package com.intrbiz.virt.router.guest;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.metadata.Var;
import com.intrbiz.virt.VirtHostApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.MachineVolume;
import com.intrbiz.virt.model.Network;
import com.intrbiz.virt.model.PersistentVolume;
import com.intrbiz.virt.model.metadata.MachineMetadataNoCloud;
import com.intrbiz.virt.model.metadata.MachineMetadataV1;
import com.intrbiz.virt.model.metadata.network.v2.EthernetNetworkV2;
import com.intrbiz.virt.model.metadata.network.v2.NameserverNetworkV2;
import com.intrbiz.virt.model.metadata.network.v2.NetworkV2;
import com.intrbiz.virt.model.metadata.network.v2.RouteV2;

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
                .withNetwork(this.network(machine))
                .withUserData(this.userData(machine))
                .withVendorData(this.vendorData(machine));
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
        // Append custom user data
        if (machine.getUserData() != null) userData.append(machine.getUserData());
        return userData.toString();
    }
    
    @Any(value="(?:vendor-data|metadata/vendor_data)", regex=true)
    @Text
    public String vendorData(@Var("machine") Machine machine)
    {
        // Our Vendor Data is always in cloud-config format
        StringBuilder userData = new StringBuilder("#cloud-config\n");
        // Phone Home
        userData.append("phone_home:\n");
        userData.append("  url: ").append(app().getMetadataServerUrl()).append("/status/boot\n");
        userData.append("  post: [ pub_key_dsa, pub_key_rsa, pub_key_ecdsa, instance_id ]\n");
        return userData.toString();
    }
    
    @Any(value="(?:network-config|metadata/network.json)", regex=true)
    @JSON
    public NetworkV2 network(@Var("machine") Machine machine)
    {
        NetworkV2 network = new NetworkV2();
        // Nameserver
        String accountName = machine.getAccount().getName();
        List<String> search = new LinkedList<String>();
        search.add(accountName + "." + app().getInternalZone());
        // TODO: search.addAll(app().getMetadataSearchDomain());
        NameserverNetworkV2 nameserver = NameserverNetworkV2.nameserver(app().getMetadataNameservers(), search);
        // eth0 - metadata and outbound nic
        EthernetNetworkV2 metadataEthernet = EthernetNetworkV2.ethernetStatic(machine.getCfgMac(), machine.getCfgIPv4() + "/16").withNameserver(nameserver);
        metadataEthernet.withRoute(new RouteV2("0.0.0.0/0", "172.16.0.1", 600));
        network.withEthernet("eth0", metadataEthernet);
        // eth1..n user nics
        int i = 1;
        for (MachineNIC nic : machine.getInterfaces())
        {
            Network net = nic.getNetwork();
            EthernetNetworkV2 ethernet = EthernetNetworkV2.ethernetStatic(nic.getMac(), nic.getIpv4() + "/" + net.getIPv4NetworkMaskBits());
            if ("public".equals(net.getPurpose()))
            {
                metadataEthernet.withRoute(new RouteV2("0.0.0.0/0", net.getIpv4Router(), 50));
            }
            network.withEthernet("eth" + i, ethernet);
            i++;
        }
        return network;
    }
    
    @Any("/metadata/")
    @Text
    public String index(@Var("machine") Machine machine)
    {
        return "/metadata/id\n" +
               "/metadata/zone\n" +
               "/metadata/name\n" +
               "/metadata/hostname\n" +
               "/metadata/cfg_mac\n" +
               "/metadata/cfg_ipv4\n" +
               "/metadata/public_keys\n" +
               "/metadata/type/name\n" +
               "/metadata/type/family\n" +
               "/metadata/type/cpus\n" +
               "/metadata/type/memory\n" +
               "/metadata/type/volume_limit\n" +
               "/metadata/type/volume_count\n" +
               "/metadata/type/ephemeral_volume_count\n" +
               "/metadata/type/ephemeral_volumes\n" +
               "/metadata/type/nic_limit\n" +
               "/metadata/image/name\n" +
               "/metadata/image/provider\n" +
               "/metadata/image/vendor\n" +
               "/metadata/image/product\n" +
               "/metadata/image/size\n" +
               "/metadata/image/open\n" +
               "/metadata/nic/\n" +
               "/metadata/network/\n" +
               "/metadata/volume/\n" +
               "/metadata/account/id\n" +
               "/metadata/account/name\n" +
               "/metadata/host/name\n" +
               "/metadata/host/placement_group\n" +
               "/metadata/user_data\n" +
               "/metadata/vendor_data\n";
               
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
    
    @Any("/metadata/cfg_ipv4")
    @Text
    public String cfgIpv4(@Var("machine") Machine machine)
    {
        return machine.getCfgIPv4();
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
    
    @Any("/metadata/nic/")
    @Text
    public String machineNics(@Var("machine") Machine machine)
    {
        StringBuilder nics = new StringBuilder();
        nics.append("eth0 static mac " + machine.getCfgMac() + " addr " + machine.getCfgIPv4() + " net metadata\n");
        for (MachineNIC nic : machine.getInterfaces())
        {
            Network net = nic.getNetwork();
            nics.append(nic.getName()).append(" static mac ").append(nic.getMac()).append(" addr ").append(nic.getIpv4()).append(" ").append(" gw ").append(net.getIpv4Router()).append(" net ").append(net.getName()).append("\n");
        }
        return nics.toString();
    }
    
    @Any("/metadata/volume/")
    @Text
    public String machineVolumes(@Var("machine") Machine machine)
    {
        StringBuilder vols = new StringBuilder();
        for (MachineVolume vol : machine.getVolumes())
        {
            vols.append(vol.getName());
            PersistentVolume pvol = vol.getAttached();
            if (pvol != null)
            {
                vols.append(" size ").append(pvol.getSize()).append(" attached ").append(pvol.getName());
            }
            vols.append("\n");
        }
        return vols.toString();
    }
    
    @Any("/metadata/network/")
    @Text
    public String machineNetworks(@Var("machine") Machine machine)
    {
        return machine.getInterfaces().stream()
                .map(i -> i.getNetwork()).collect(Collectors.toSet()).stream()
                .map(n -> n.getName()).collect(Collectors.joining("\n"));
    }
    
    @Any("/metadata/network/:name/hosts")
    @Text
    @WithDataAdapter(VirtDB.class)
    public String machineNetworks(VirtDB db, @Var("machine") Machine machine, String name)
    {
        
        return "";
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
    
    @Any("/metadata/host/name")
    @Text
    public String hostName()
    {
        return app().getConfiguration().getName();
    }
    
    @Any("/metadata/host/placement_group")
    @Text
    public String hostPlacementGroup()
    {
        return app().getConfiguration().getPlacementGroup();
    }
}
