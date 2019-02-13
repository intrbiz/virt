package com.intrbiz.vpp;

import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.BridgeDomainDetail;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;

public class Example
{
    public static void main(String[] args) throws Exception
    {
        System.out.println(IPv4CIDR.fromString("10.250.0.0/20"));
        System.out.println(IPv4CIDR.fromString("10.250.0.0/20").getNetmask());
        System.out.println(IPv4CIDR.fromString("10.250.0.0/20").getAddress());
        System.out.println(IPv4CIDR.fromString("10.250.0.0/20").getBroadcast());
        System.exit(1);
        //
        IPv4CIDR host = IPv4CIDR.fromString("172.18.0.2/24");
        MACAddress hostHw = MACAddress.random();
        System.out.println("Host " + host + " " + hostHw);
        //
        IPv4CIDR router = IPv4CIDR.fromString("10.0.42.1/24");
        MACAddress routerHw = MACAddress.random();
        System.out.println("Router " + router + " " + routerHw);
        try (VPPSimple simple = VPPSimple.connect("VPPTest"))
        {
            // VPP version
            System.out.println("Connected to VPP:");
            System.out.println(simple.info().getVersion().get());
            // Graph node lookup
            System.out.println("Lookup L2 graph node index");
            System.out.println(simple.graph().nodeIndex("l2-input").get());
            // Create a host interface
            System.out.println("Creating host interface:");
            InterfaceIndex created = simple.interfaces().createHostInterface("vpp1host", hostHw).get();
            System.out.println(created);
            // Set interface address
            System.out.println("Set host interface IPv4 address:");
            simple.interfaces().addInterfaceIPv4Address(created, host).get();
            // Set interface up
            System.out.println("Set host interface up:");
            simple.interfaces().setInterfaceUp(created).get();
            // Create a bridge domain
            System.out.println("Create Bridge Domain:");
            simple.bridge().createBridgeDomain(new BridgeDomainId(42), true, true, true, true, false, 5, new Tag("bridge:42")).get();
            simple.bridge().createBridgeDomain(new BridgeDomainId(43), true, true, true, true, false, 5, new Tag("bridge:43")).get();
            // List bridge domains
            System.out.println("Bridge domains:");
            for (BridgeDomainDetail bdom : simple.bridge().listBridgeDomains().get())
            {
                System.out.println(bdom);
            }
            // Add VXLAN tunnel
            System.out.println("Creating VXLAN tunnel:");
            //InterfaceIndex tunnel = simple.vxlan().createVXLANTunnel(new VXLANId(42), host.getAddress(), IPv4Address.fromString("239.1.1.1"), created).get();
            //simple.interfaces().setMTU(tunnel, 1500);
            //simple.bridgeDomain().addInterfaceToBridgeDomain(new BridgeDomainId(42), tunnel);
            // Create BVI interface
            System.out.println("Creating Loop BVI:");
            // 42
            InterfaceIndex loop42 = simple.interfaces().createLoopbackInterface(routerHw).get();
            simple.interfaces().addInterfaceIPv4Address(loop42, router);
            simple.bridge().addInterfaceToBridgeDomain(new BridgeDomainId(42), loop42, true, SplitHorizonGroup.ONE);
            simple.interfaces().setInterfaceUp(loop42).get();
            // 43
            InterfaceIndex loop43 = simple.interfaces().createLoopbackInterface(routerHw).get();
            simple.interfaces().addInterfaceIPv4Address(loop43, router);
            simple.bridge().addInterfaceToBridgeDomain(new BridgeDomainId(43), loop43, true, SplitHorizonGroup.ONE);
            simple.interfaces().setInterfaceUp(loop43).get();
            // List interfaces
            System.out.println("Listing interfaces:");
            for (InterfaceDetail iface : simple.interfaces().listInterfaces().get())
            {
                System.out.println(iface);
            }
            System.out.println(simple.interfaces().getHostInterface("vpp1host").get());
        }
    }
}
