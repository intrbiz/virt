package com.intrbiz.system.exec;

public class SystemExecutionException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public SystemExecutionException()
    {
        super();
    }

    public SystemExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SystemExecutionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SystemExecutionException(String message)
    {
        super(message);
    }

    public SystemExecutionException(Throwable cause)
    {
        super(cause);
    }    
}
