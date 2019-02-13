package com.intrbiz.system.exec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Logger;

public class DefaultSystemExecutorService implements SystemExecutorService
{
    private static Logger logger = Logger.getLogger(DefaultSystemExecutorService.class);
    
    public Result exec(Command command) throws SystemExecutionException
    {
        logger.debug("Executing: "+ command);
        try
        {
            Process proc = new ProcessBuilder(command.buildCommandLine()).start();
            int exit = proc.waitFor();
            Result result = new Result(exit, readStream(proc.getInputStream()), readStream(proc.getErrorStream()));
            logger.debug("Exited with: " + result);
            return result;
        }
        catch (IOException | InterruptedException e)
        {
            logger.debug("Errored with: " + e.getMessage(), e);
            throw new SystemExecutionException("Error executing process " + command, e);
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
}
