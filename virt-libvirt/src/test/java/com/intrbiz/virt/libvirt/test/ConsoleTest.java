package com.intrbiz.virt.libvirt.test;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.libvirt.Stream;

import com.intrbiz.virt.libvirt.LibVirtAdapter;
import com.intrbiz.virt.libvirt.model.wrapper.LibVirtDomain;

public class ConsoleTest
{
    public static void main(String[] args) throws Exception
    {
        try (LibVirtAdapter lv = LibVirtAdapter.qemu.ssh.connect("root", "vm1"))
        {
            LibVirtDomain domain = lv.lookupDomainByName("dns2.core.fluxstac.net");
            final Stream console = domain.getLibVirtDomain().openConsole();
            // damn java and its buffered io
            new Thread(new Runnable() {
                public void run()
                {
                    WritableByteChannel out = Channels.newChannel(System.out);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (console.isOpen())
                    {
                        try
                        {
                            buffer.rewind();
                            console.read(buffer);
                            buffer.flip();
                            while (buffer.hasRemaining())
                            {
                                out.write(buffer);
                            }
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
            }).start();
            new Thread(new Runnable() {
                public void run()
                {
                    ReadableByteChannel in = Channels.newChannel(System.in);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (console.isOpen())
                    {
                        try
                        {
                            buffer.rewind();
                            in.read(buffer);
                            buffer.flip();
                            while (buffer.hasRemaining())
                            {
                                console.write(buffer);
                            }
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
            }).start();
            //
            System.out.println("Console opened");
        }
    }
}
