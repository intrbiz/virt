package com.intrbiz.virt.manager.store;

import com.ceph.rados.IoCTX;
import com.ceph.rados.Rados;
import com.ceph.rbd.Rbd;

public class RadosTest
{
    public static void main(String[] args) throws Exception
    {
        Rados rados = new Rados("admin");
        rados.connect();
        IoCTX poolIo = rados.ioCtxCreate("machine-1");
        try
        {
            Rbd rbd = new Rbd(poolIo);
            for (String name : rbd.list())
            {
                System.out.println(name);
            }
        }
        finally
        {
            rados.ioCtxDestroy(poolIo);
        }
    }
}
