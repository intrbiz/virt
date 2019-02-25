package com.intrbiz.system.exec;

import java.util.regex.Pattern;

public interface SystemExecutorService
{    
    Result exec(Command command) throws SystemExecutionException;
    
    default void expect(Command command, Pattern expectedOutput, int... successCodes) throws SystemExecutionException
    {
        Result result = this.exec(command);
        if (! (result.isSuccess(successCodes) || result.expectOutput(expectedOutput)))
            throw new SystemExecutionException("Failed to execute: " + command + " exit: " + result.getExit() + "\n " + result.getError());
    }
    
    default void expect(Command command, int... successCodes) throws SystemExecutionException
    {
        Result result = this.exec(command);
        if (! result.isSuccess(successCodes))
            throw new SystemExecutionException("Failed to execute: " + command + " exit: " + result.getExit() + "\n " + result.getError());
    }
    
    default void expect(Command command)
    {
        expect(command, Result.DEFAULT_SUCCESS_EXIT_CODE);
    }
    
    default void fireAndForget(Command command)
    {
        try
        {
            this.exec(command);
        }
        catch (SystemExecutionException e)
        {
            // ignore
        }
    }
    
    static SystemExecutorService getSystemExecutorService()
    {
        return Boolean.getBoolean("intrbiz.exec.sudo") ? new SudoSystemExecutorService() : new DefaultSystemExecutorService();
    }
}
