package com.intrbiz.virt.libvirt.test;

import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.definition.GraphicsDef;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDisk;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDiskInfo;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDiskStats;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtInterface;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtInterfaceStats;

public class ListGuests
{
    public static void main(String[] args) throws Exception
    {
        try (LibVirtAdapter lv = LibVirtAdapter.qemu.ssh.connect("root","localhost"))
        {
            for (int i = 0; i < 10; i++)
            {
                for (LibVirtDomain dom : lv.listDomains())
                {
                    System.out.println("Domain: " + dom.getName() + " {" + dom.getUUID() + "} running: " + dom.isRunning());
                    for (LibVirtDisk disk : dom.getDisks())
                    {
                        System.out.println("  Disk: " + disk.getDevice() + " " + disk.getTargetBus() + "." + disk.getTargetName() + " -> " + disk.getSourceUrl());
                        LibVirtDiskInfo info = disk.getDiskInfo();
                        if (info != null) System.out.println("    capacity: " + info.getCapacity() + " allocated: " + info.getAllocated() + " physical: " + info.getPhysical());
                        LibVirtDiskStats stats = disk.getDiskStats();
                        if (stats != null) System.out.println("    read: " + stats.getRdBytes() + " " + stats.getRdReq() + " write: " + stats.getWrBytes() + " " + stats.getWrReq());
                    }
                    for (LibVirtInterface iface : dom.getInterfaces())
                    {
                        System.out.println("  Interface: " + iface.getType() + " " + iface.getMacAddress() + " -> " + iface.getBridge() + "::" + iface.getName());
                        LibVirtInterfaceStats stats = iface.getInterfaceStats();
                        if (stats != null) System.out.println("    rx: " + stats.getRxBytes() + " tx: " + stats.getTxBytes());
                    }
                    for (GraphicsDef gfx : dom.getDomainDef().getDevices().getGraphics())
                    {
                        System.out.println("  Graphics: auto:" + gfx.getAutoport() + " " + gfx.getListen() + ":" + gfx.getPort() + " websocket=" + gfx.getWebsocket());
                    }
                    System.out.println();
                }
                Thread.sleep(1000);
                System.gc();
            }
        }
    }
}
