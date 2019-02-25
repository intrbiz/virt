package com.intrbiz.vpp;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.intrbiz.vpp.api.VPPRecipeManager;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.VNI;
import com.intrbiz.vpp.recipe.Bridge;
import com.intrbiz.vpp.recipe.BridgeInterface;
import com.intrbiz.vpp.recipe.HostInterface;
import com.intrbiz.vpp.recipe.VXLANTunnel;
import com.intrbiz.vpp.recipe.VethHostInterface;

public class RecipeExample
{
    public static void main(String[] args) throws Exception
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.TRACE);
        // host recipe
        /*
        // VMHost host = new VMHost(args[0], args[1], IPv4CIDR.fromString(args[2]), VMInterfaceType.VETH);
        VMNetworks host = new VMNetworks(args[0], IPv4CIDR.fromString(args[2]), VMInterfaceType.VETH);
        host.addNetwork(new VMNetworkId(142));
        host.addNetwork(new VMNetworkId(143));
        host.addRemoteVMHost(IPv4Address.fromString(args[3]));
        host.addRemoteVMHost(IPv4Address.fromString("172.16.40.12"));
        // add some vms
        //host.addVM(MACAddress.fromString("6a:fb:fb:35:81:1a"), MACAddress.fromString("7a:fb:fb:35:81:1a"), new VMNetworkId(142));
        //host.addVM(MACAddress.fromString("6a:fb:fb:35:81:1b"), MACAddress.fromString("7a:fb:fb:35:81:1b"), new VMNetworkId(142));
        //host.addVM(MACAddress.fromString("6a:fb:fb:35:81:1c"), MACAddress.fromString("7a:fb:fb:35:81:1c"), new VMNetworkId(143));
        host.addVM(MACAddress.fromString("7a:fb:fb:35:81:1a"), new VMNetworkId(142));
        host.addVM(MACAddress.fromString("7a:fb:fb:35:81:1b"), new VMNetworkId(142));
        host.addVM(MACAddress.fromString("7a:fb:fb:35:81:1c"), new VMNetworkId(143));
        */
        IPv4CIDR source = IPv4CIDR.fromString("172.16.40.12/24");
        IPv4Address host1 = IPv4Address.fromString("172.16.40.13");
        IPv4Address host2 = IPv4Address.fromString("172.16.40.14");
        //
        HostInterface interconnect = new HostInterface("interconnect", "enp1s0", MTU.JUMBO, source);
        Bridge br142 = new Bridge(new BridgeDomainId(142));
        Bridge br143 = new Bridge(new BridgeDomainId(142));
        //
        VXLANTunnel vxlan142toh1 = new VXLANTunnel(source.getAddress(), host1, new VNI(142));
        VXLANTunnel vxlan142toh2 = new VXLANTunnel(source.getAddress(), host2, new VNI(142));
        VXLANTunnel vxlan143toh1 = new VXLANTunnel(source.getAddress(), host1, new VNI(143));
        VXLANTunnel vxlan143toh2 = new VXLANTunnel(source.getAddress(), host2, new VNI(143));
        BridgeInterface br142vxlan142toh1 = new BridgeInterface(br142, vxlan142toh1);
        BridgeInterface br142vxlan142toh2 = new BridgeInterface(br142, vxlan142toh2);
        BridgeInterface br143vxlan143toh1 = new BridgeInterface(br143, vxlan143toh1);
        BridgeInterface br143vxlan143toh2 = new BridgeInterface(br143, vxlan143toh2);
        //
        VethHostInterface vm1 = VethHostInterface.forVM(MACAddress.fromString("6a:fb:fb:35:81:1a"));
        VethHostInterface vm2 = VethHostInterface.forVM(MACAddress.fromString("6a:fb:fb:35:81:1b"));
        VethHostInterface vm3 = VethHostInterface.forVM(MACAddress.fromString("6a:fb:fb:35:81:1c"));
        BridgeInterface br142vm1 = new BridgeInterface(br142, vm1);
        BridgeInterface br142vm2 = new BridgeInterface(br142, vm2);
        BridgeInterface br143vm3 = new BridgeInterface(br143, vm3);
        //
        System.out.println(interconnect);
        System.out.println(br142);
        System.out.println(br143);
        //
        System.out.println(vxlan142toh1);
        System.out.println(vxlan142toh2);
        System.out.println(vxlan143toh1);
        System.out.println(vxlan143toh2);
        System.out.println(br142vxlan142toh1);
        System.out.println(br142vxlan142toh2);
        System.out.println(br143vxlan143toh1);
        System.out.println(br143vxlan143toh2);
        //
        System.out.println(vm1);
        System.out.println(vm2);
        System.out.println(vm3);
        System.out.println(br142vm1);
        System.out.println(br142vm2);
        System.out.println(br143vm3);
        //
        File store = new File("store");
        store.mkdirs();
        try (VPPRecipeManager manager = VPPRecipeManager.connect("VPPTest", store))
        {
            // register
            manager.update(interconnect);
            manager.update(br142);
            manager.update(br143);
            //
            manager.update(vxlan142toh1);
            manager.update(vxlan142toh2);
            manager.update(vxlan143toh1);
            manager.update(vxlan143toh2);
            manager.update(br142vxlan142toh1);
            manager.update(br142vxlan142toh2);
            manager.update(br143vxlan143toh1);
            manager.update(br143vxlan143toh2);
            //
            manager.update(vm1);
            manager.update(vm2);
            manager.update(vm3);
            manager.update(br142vm1);
            manager.update(br142vm2);
            manager.update(br143vm3);
            //
            manager.update();
        }
    }
}
