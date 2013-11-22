package com.intrbiz.virt.libvirt.test;

import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtHostInterface;

public class ListHostInterfaces
{
    public static void main(String[] args)
    {
        try (LibVirtAdapter lv = LibVirtAdapter.qemu.ssh.connect("root", "localhost"))
        {
            for (LibVirtHostInterface hif : lv.listHostInterfaces())
            {
                System.out.println("Host Interface: " + hif.getName() + " " + hif.getMacAddress());
            }
        }
    }
}
