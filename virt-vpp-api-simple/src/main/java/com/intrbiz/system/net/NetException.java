package com.intrbiz.system.net;

public class NetException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public NetException()
    {
        super();
    }

    public NetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NetException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NetException(String message)
    {
        super(message);
    }

    public NetException(Throwable cause)
    {
        super(cause);
    }
}
