package com.intrbiz.system.exec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Result
{
    public static final int DEFAULT_SUCCESS_EXIT_CODE = 0;
    
    private final int exit;
    
    private final String out;
    
    private final String error;

    public Result(int exit, String out, String error)
    {
        super();
        this.exit = exit;
        this.out = out;
        this.error = error;
    }

    public int getExit()
    {
        return exit;
    }
    
    public boolean isSuccess(int... successCodes)
    {
        if (successCodes == null || successCodes.length == 0)
            return true;
        for (int code : successCodes)
        {
            if (this.exit == code)
                return true;
        }
        return false;
    }
    
    public boolean isSuccess()
    {
        return this.isSuccess(DEFAULT_SUCCESS_EXIT_CODE);
    }
    
    public boolean expectOutput(Pattern pattern)
    {
        Matcher m = pattern.matcher(this.out);
        return m.matches();
    }
    
    public boolean expectError(Pattern pattern)
    {
        Matcher m = pattern.matcher(this.error);
        return m.matches();
    }

    public String getOut()
    {
        return out;
    }

    public String getError()
    {
        return error;
    }
    
    public String toString()
    {
        return this.exit + ": " + this.out;
    }
}
