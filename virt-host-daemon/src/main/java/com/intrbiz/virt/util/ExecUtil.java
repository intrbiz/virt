package com.intrbiz.virt.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.virt.VirtError;

public class ExecUtil
{
    private static Logger logger = Logger.getLogger(ExecUtil.class);
    
    public static void run(String command, List<String> arguments)
    {
        ExecResult res = exec(command, arguments);
        if (res.exit != 0)
            throw new VirtError("Failed to execute: " + command + " exit: " + res.exit + "\n " + res.error);
    }
    
    public static void assume(String command, List<String> arguments)
    {
        ExecResult res = exec(command, arguments);
        logger.debug("Result: exit=" + res.exit + "\n" + res.out + "\n" + res.error);
    }
    
    public static ExecResult exec(String command, List<String> arguments)
    {
        List<String> cmd = new LinkedList<String>();
        cmd.add(command);
        cmd.addAll(arguments);
        logger.debug("Executing: "+ cmd.stream().collect(Collectors.joining(" ")));
        try
        {
            Process proc = new ProcessBuilder(cmd).start();
            int exit = proc.waitFor();
            String out = readStream(proc.getInputStream());
            String err = readStream(proc.getErrorStream());
            return new ExecResult(exit, out, err);
        }
        catch (IOException | InterruptedException e)
        {
            throw new VirtError("Error executing process " + command + "(" + arguments + ")", e);
        }
    }
    
    private static String readStream(InputStream in) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new InputStreamReader(in))
        {
            int read;
            char[] buf = new char[1024];
            while ((read = reader.read(buf)) != -1)
            {
                sb.append(buf, 0, read);
            }
        }
        return sb.toString();
    }
    
    public static class ExecResult
    {
        public int exit;
        
        public String out;
        
        public String error;
        
        public ExecResult(int exit, String out, String error)
        {
            this.exit = exit;
            this.out = out;
            this.error = error;
        }
    }
}
