package com.intrbiz.system.exec;

public final class Sudo
{
    private static final String SUDO = "/usr/bin/sudo";
    
    public static final Command sudo(Command command)
    {
        Command sudoed = new Command(SUDO);
        sudoed.arg(command.getCommand());
        for (String arg : command.getArguments())
        {
            sudoed.arg(arg);
        }
        return sudoed;
    }
}
