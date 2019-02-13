package com.intrbiz.system.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Command
{
    private final String command;
    
    private final List<String> arguments = new ArrayList<>();
    
    public Command(String command)
    {
        this.command = Objects.requireNonNull(command);
    }
    
    public Command(String command, String... arguments)
    {
        this(command);
        if (arguments != null) {
            for (String argument : arguments) {
                this.arguments.add(argument);
            }
        }
    }
    
    public Command(String command, List<String> arguments)
    {
        this(command);
        if (arguments != null) {
            this.arguments.addAll(arguments);
        }
    }
    
    public String getCommand()
    {
        return this.command;
    }
    
    public List<String> getArguments()
    {
        return this.arguments;
    }
    
    public Command arg(String argument)
    {
        this.arguments.add(argument);
        return this;
    }
    
    public List<String> buildCommandLine()
    {
        List<String> line = new ArrayList<>(this.arguments.size() + 1);
        line.add(this.command);
        line.addAll(this.arguments);
        return line;
    }
    
    public String toString()
    {
        if (this.arguments.isEmpty())
            return this.command;
        return this.command + " " + this.arguments.stream().map(s -> "'" + s + "'").collect(Collectors.joining(" "));
    }
    
    public static final Command command(String command)
    {
        return new Command(command);
    }
    
    public static final Command command(String command, String... args)
    {
        return new Command(command, args);
    }
    
    public static final Command command(String command, List<String> args)
    {
        return new Command(command, args);
    }
}
