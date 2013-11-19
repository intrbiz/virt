package com.intrbiz.virt.libvirt.test;

import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtNodeInfo;

public class NodeInfo
{
    public static void main(String[] args)
    {
        try (LibVirtAdapter lv = LibVirtAdapter.sshConnect("localhost"))
        {
            LibVirtNodeInfo node = lv.nodeInfo();
            System.out.println("Model: " + node.getModel());
            System.out.println("CPUs: " + node.getCpus());
            System.out.println("MHz: " + node.getMhz());
            System.out.println("Memory: " + node.getMemory());
            System.out.println("Nodes: " + node.getNodes());
            System.out.println("Sockets: " + node.getSockets());
            System.out.println("Cores: " + node.getCores());
            System.out.println("Threads: " + node.getThreads());
        }
    }
}
