package com.intrbiz.virt.libvirt.model.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class IdedWeakReference<T> extends WeakReference<T>
{
    private final int id;

    public IdedWeakReference(int id, T referent, ReferenceQueue<? super T> q)
    {
        super(referent, q);
        this.id = id;
    }

    public IdedWeakReference(int id, T referent)
    {
        super(referent);
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
}
