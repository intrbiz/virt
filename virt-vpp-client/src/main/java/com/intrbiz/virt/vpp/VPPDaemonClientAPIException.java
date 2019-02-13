package com.intrbiz.virt.vpp;

public class VPPDaemonClientAPIException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public VPPDaemonClientAPIException()
    {
        super();
    }

    public VPPDaemonClientAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public VPPDaemonClientAPIException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public VPPDaemonClientAPIException(String message)
    {
        super(message);
    }

    public VPPDaemonClientAPIException(Throwable cause)
    {
        super(cause);
    }
}
