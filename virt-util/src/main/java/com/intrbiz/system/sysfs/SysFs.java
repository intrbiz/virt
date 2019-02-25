package com.intrbiz.system.sysfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class SysFs
{
    public static class INTERFACE_FLAGS
    {
        public static final long UP = 1 << 0;

        public static final long BROADCAST = 1 << 1;

        public static final long DEBUG = 1 << 2;

        public static final long LOOPBACK = 1 << 3;

        public static final long POINTOPOINT = 1 << 4;

        public static final long NOTRAILERS = 1 << 5;

        public static final long RUNNING = 1 << 6;

        public static final long NOARP = 1 << 7;

        public static final long PROMISC = 1 << 8;

        public static final long ALLMULTI = 1 << 9;

        public static final long MASTER = 1 << 10;

        public static final long SLAVE = 1 << 11;

        public static final long MULTICAST = 1 << 12;

        public static final long PORTSEL = 1 << 13;

        public static final long AUTOMEDIA = 1 << 14;

        public static final long DYNAMIC = 1 << 15;

        public static final long LOWER_UP = 1 << 16;

        public static final long DORMANT = 1 << 17;

        public static final long ECHO = 1 << 18;
    }

    private static final Logger logger = Logger.getLogger(SysFs.class);

    private static final String nr_hugepages = "nr_hugepages";

    private static final String free_hugepages = "free_hugepages";

    private static final SysFs DEFAULT_SYS_FS = new SysFs();

    public static SysFs sysFs()
    {
        return DEFAULT_SYS_FS;
    }

    private final File base;

    private final File kernel;

    private final File kernel_mm;

    private final File kernel_mm_hugepages;

    private final File kernel_mm_hugepages_hugepages_2048kB;

    private final File kernel_mm_hugepages_hugepages_1048576kB;

    private final File clazz;

    private final File class_net;

    private SysFs(File base)
    {
        this.base = base;
        this.kernel = new File(this.base, "kernel");
        this.kernel_mm = new File(this.kernel, "mm");
        this.kernel_mm_hugepages = new File(this.kernel_mm, "hugepages");
        this.kernel_mm_hugepages_hugepages_2048kB = new File(this.kernel_mm_hugepages, "hugepages-2048kB");
        this.kernel_mm_hugepages_hugepages_1048576kB = new File(this.kernel_mm_hugepages, "hugepages-1048576kB");
        this.clazz = new File(this.base, "class");
        this.class_net = new File(this.clazz, "net");
    }

    private SysFs()
    {
        this(new File("/sys"));
    }

    public long getHugepages2MiBTotal()
    {
        return readLongValue(new File(this.kernel_mm_hugepages_hugepages_2048kB, nr_hugepages));
    }

    public long getHugepages2MiBFree()
    {
        return readLongValue(new File(this.kernel_mm_hugepages_hugepages_2048kB, free_hugepages));
    }

    public long getHugepages1GiBTotal()
    {
        return readLongValue(new File(this.kernel_mm_hugepages_hugepages_1048576kB, nr_hugepages));
    }

    public long getHugepages1GiBFree()
    {
        return readLongValue(new File(this.kernel_mm_hugepages_hugepages_1048576kB, free_hugepages));
    }

    public List<String> getInterfaces()
    {
        List<String> ifaces = new LinkedList<>();
        File[] files = this.class_net.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                ifaces.add(file.getName());
            }
        }
        return ifaces;
    }
    
    public boolean interfaceExists(String interfaceName)
    {
        File interfaceDir = new File(this.class_net, interfaceName);
        return interfaceDir.exists() && interfaceDir.isDirectory();
    }

    public String getInterfaceMAC(String interfaceName)
    {
        File interfaceDir = new File(this.class_net, interfaceName);
        if (interfaceDir.exists() && interfaceDir.isDirectory()) return readValue(new File(interfaceDir, "address"));
        return null;
    }

    public int getInterfaceMTU(String interfaceName)
    {
        File interfaceDir = new File(this.class_net, interfaceName);
        if (interfaceDir.exists() && interfaceDir.isDirectory()) return readIntValue(new File(interfaceDir, "mtu"));
        return 0;
    }

    public long getInterfaceFlags(String interfaceName)
    {
        File interfaceDir = new File(this.class_net, interfaceName);
        if (interfaceDir.exists() && interfaceDir.isDirectory()) { return readLongHexValue(new File(interfaceDir, "flags")); }
        return 0L;
    }

    public boolean isInterfacePromiscuous(String interfaceName)
    {
        long flags = this.getInterfaceFlags(interfaceName);
        return (flags & INTERFACE_FLAGS.PROMISC) == INTERFACE_FLAGS.PROMISC;
    }

    public boolean isInterfaceUp(String interfaceName)
    {
        long flags = this.getInterfaceFlags(interfaceName);
        return (flags & INTERFACE_FLAGS.UP) == INTERFACE_FLAGS.UP;
    }

    private static long readLongHexValue(File file)
    {
        String value = readValue(file);
        return value == null ? -1L : Long.parseLong(value.substring(2), 16);
    }

    private static long readLongValue(File file)
    {
        String value = readValue(file);
        return value == null ? -1L : Long.parseLong(value);
    }

    private static int readIntValue(File file)
    {
        String value = readValue(file);
        return value == null ? -1 : Integer.parseInt(value);
    }

    private static String readValue(File file)
    {
        try (BufferedReader in = new BufferedReader(new FileReader(file)))
        {
            return in.readLine();
        }
        catch (IOException e)
        {
            logger.warn("Failed to read from SysFs " + file, e);
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static String readFullValue(File file)
    {
        try (FileReader in = new FileReader(file))
        {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int r;
            while ((r = in.read(buf)) != -1)
            {
                sb.append(buf, 0, r);
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            logger.warn("Failed to read from SysFs " + file, e);
        }
        return null;
    }
}
