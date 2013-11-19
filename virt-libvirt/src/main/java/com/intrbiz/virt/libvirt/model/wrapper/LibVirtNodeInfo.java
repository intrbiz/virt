package com.intrbiz.virt.libvirt.model.wrapper;

public class LibVirtNodeInfo
{
    private final String model;

    private final long memory;

    private final int cpus;

    private final int mhz;

    private final int nodes;

    private final int sockets;

    private final int cores;

    private final int threads;

    public LibVirtNodeInfo(String model, long memory, int cpus, int mhz, int nodes, int sockets, int cores, int threads)
    {
        super();
        this.model = model;
        this.memory = memory;
        this.cpus = cpus;
        this.mhz = mhz;
        this.nodes = nodes;
        this.sockets = sockets;
        this.cores = cores;
        this.threads = threads;
    }

    public String getModel()
    {
        return model;
    }

    public long getMemory()
    {
        return memory;
    }

    public int getCpus()
    {
        return cpus;
    }

    public int getMhz()
    {
        return mhz;
    }

    public int getNodes()
    {
        return nodes;
    }

    public int getSockets()
    {
        return sockets;
    }

    public int getCores()
    {
        return cores;
    }

    public int getThreads()
    {
        return threads;
    }
}
