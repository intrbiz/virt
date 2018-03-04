package com.intrbiz.virt;

public class VirtError extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public VirtError()
    {
        super();
    }

    public VirtError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public VirtError(String message, Throwable cause)
    {
        super(message, cause);
    }

    public VirtError(String message)
    {
        super(message);
    }

    public VirtError(Throwable cause)
    {
        super(cause);
    }
}
