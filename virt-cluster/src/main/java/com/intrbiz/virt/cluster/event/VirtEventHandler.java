package com.intrbiz.virt.cluster.event;

import com.intrbiz.virt.event.VirtEvent;

public interface VirtEventHandler<T extends VirtEvent>
{
    void process(T event);
}
