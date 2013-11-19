package com.intrbiz.virt.libvirt.model.wrapper;

public class LibVirtDiskStats
{
    private final long rdReq;

    private final long rdBytes;

    private final long wrReq;

    private final long wrBytes;

    private final long errs;

    public LibVirtDiskStats(long rdReq, long rdBytes, long wrReq, long wrBytes, long errs)
    {
        super();
        this.rdReq = rdReq;
        this.rdBytes = rdBytes;
        this.wrReq = wrReq;
        this.wrBytes = wrBytes;
        this.errs = errs;
    }

    public long getRdReq()
    {
        return rdReq;
    }

    public long getRdBytes()
    {
        return rdBytes;
    }

    public long getWrReq()
    {
        return wrReq;
    }

    public long getWrBytes()
    {
        return wrBytes;
    }

    public long getErrs()
    {
        return errs;
    }

}
