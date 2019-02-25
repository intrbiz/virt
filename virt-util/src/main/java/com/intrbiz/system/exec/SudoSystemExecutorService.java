package com.intrbiz.system.exec;

public class SudoSystemExecutorService extends DefaultSystemExecutorService
{    
    public Result exec(Command command) throws SystemExecutionException
    {
        return super.exec(Sudo.sudo(command));
    }
}
