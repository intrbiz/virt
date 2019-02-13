package com.intrbiz.vpp;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.intrbiz.vpp.api.VPPRecipeManager;
import com.intrbiz.vpp.api.model.IPv4Address;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.recipe.VMInterfaceType;
import com.intrbiz.vpp.recipe.VMNetworkId;
import com.intrbiz.vpp.recipe.VMNetworks;

public class RecipeExample
{
    public static void main(String[] args) throws Exception
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.TRACE);
        // host recipe
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
        //
        System.out.println(host);
        //
        File store = new File("store");
        store.mkdirs();
        try (VPPRecipeManager manager = VPPRecipeManager.connect("VPPTest", store))
        {
            // register
            manager.update("vm.host", host);
        }
    }
}
