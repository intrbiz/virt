package com.intrbiz.virt.libvirt.test;

import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStoragePool;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtStorageVol;

public class ListStorage
{
    public static void main(String[] args) throws Exception
    {
        try (LibVirtAdapter lv = LibVirtAdapter.qemu.ssh.connect("root","localhost"))
        {
            for (LibVirtStoragePool pool : lv.listStoragePools())
            {
                System.out.println("Storage pool: " + pool.getName() + " " + pool.getUUID());
                for (LibVirtStorageVol vol : pool.listVolumes())
                {
                    System.out.println("  Storage vol: " + vol.getName() + " " + vol.getPath() + " " + vol.getStoragePool().getName());
                }
            }
        }
    }
}
