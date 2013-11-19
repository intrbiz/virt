package com.intrbiz.virt.libvirt.model.wrapper;

public class LibVirtInterfaceStats
{
    private final long rxBytes;

    private final long rxPackets;

    private final long rxErrs;

    private final long rxDrop;

    private final long txBytes;

    private final long txPackets;

    private final long txErrs;

    private final long txDrop;

    public LibVirtInterfaceStats(long rx_bytes, long rx_packets, long rx_errs, long rx_drop, long tx_bytes, long tx_packets, long tx_errs, long tx_drop)
    {
        super();
        this.rxBytes = rx_bytes;
        this.rxPackets = rx_packets;
        this.rxErrs = rx_errs;
        this.rxDrop = rx_drop;
        this.txBytes = tx_bytes;
        this.txPackets = tx_packets;
        this.txErrs = tx_errs;
        this.txDrop = tx_drop;
    }

    public long getRxBytes()
    {
        return rxBytes;
    }

    public long getRxPackets()
    {
        return rxPackets;
    }

    public long getRxErrs()
    {
        return rxErrs;
    }

    public long getRxDrop()
    {
        return rxDrop;
    }

    public long getTxBytes()
    {
        return txBytes;
    }

    public long getTxPackets()
    {
        return txPackets;
    }

    public long getTxErrs()
    {
        return txErrs;
    }

    public long getTxDrop()
    {
        return txDrop;
    }
}
