package com.intrbiz.virt.libvirt.test;

import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.event.LibVirtDomainLifecycleEventHandler;
import com.intrbiz.virt.libvirt.event.LibVirtDomainRebootEventHandler;
import com.intrbiz.virt.libvirt.model.event.LibVirtDomainLifecycle;
import com.intrbiz.virt.libvirt.model.event.LibVirtDomainReboot;

public class EventTest
{
    public static void main(String[] args) throws Exception
    {
        LibVirtAdapter lv = LibVirtAdapter.qemu.tcp.connect("172.30.13.30", 5005);
        // register for events
        lv.registerEventHandler(new LibVirtDomainLifecycleEventHandler() {
            @Override
            public void onEvent(LibVirtDomainLifecycle event)
            {
                System.out.println("Got event: " + event);
            }
        });
        lv.registerEventHandler(new LibVirtDomainRebootEventHandler() {
            @Override
            public void onEvent(LibVirtDomainReboot event)
            {
                System.out.println("Got event: " + event);
            }
        });
    }
}
