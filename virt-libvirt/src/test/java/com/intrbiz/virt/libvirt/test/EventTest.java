package com.intrbiz.virt.libvirt.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.event.LibVirtDomainLifecycleEventHandler;
import com.intrbiz.virt.libvirt.model.event.LibVirtDomainLifecycle;

public class EventTest
{
    public static void main(String[] args) throws Exception
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.TRACE);
        //
        LibVirtAdapter lv = LibVirtAdapter.qemu.tcp.connect("172.30.13.30", 5005);
        System.out.println("Registering event handler");
        lv.registerEventHandler(new LibVirtDomainLifecycleEventHandler()
        {
            @Override
            public void onEvent(LibVirtDomainLifecycle event)
            {
                System.out.println("Main Got event: " + event);
            }
        });
        System.out.println("Listening");
    }
}
