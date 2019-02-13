package com.intrbiz.virt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;

public class PDNSBackend
{
    private BufferedReader input;
    
    private PrintStream output;
    
    private PrintStream error;

    public PDNSBackend(InputStream in, PrintStream out, PrintStream err)
    {
        super();
        this.input = new BufferedReader(new InputStreamReader(in));
        this.output = out;
        this.error = err;
    }
    
    protected void info(String message)
    {
        this.error.println(message);
    }
    
    protected String[] readCommand()
    {
        try 
        {
            String line = this.input.readLine();
            return line == null ? null : line.split("\t");
        }
        catch (IOException e)
        {
            fail("Failed to read command", e);
        }
        return null;
    }
    
    protected void fail(String message)
    {
        this.fail(message, null);
    }
    
    protected void fail(String message, Throwable t)
    {
        this.fail();
        if (message != null) this.error.println(message);
        if (t != null) t.printStackTrace(this.error);
    }
    
    protected void fail()
    {
        this.output.print("FAIL\n");
        this.output.flush();
    }
    
    protected void ok(String message)
    {
        this.output.print("OK\t" + message + "\n");
        this.output.flush();
    }
    
    protected void data(String scopebits, String auth, String qname, String qclass, String qtype, String ttl, String id, String content)
    {
        this.output.print("DATA\t" + scopebits + "\t" + auth + "\t" + qname + "\t" + qclass + "\t" + qtype + "\t" + ttl + "\t" + id + "\t" + content + "\n");
        this.output.flush();
    }
    
    protected void end()
    {
        this.output.print("END\n");
        this.output.flush();
    }
    
    /**
     * Handshake with PDNS server
     */
    public boolean hello()
    {
        String[] command = this.readCommand();
        if (command != null && command.length == 2 && "HELO".equals(command[0]) && "3".equals(command[1]))
        {
            this.ok("Intrbiz Virt PDNS Backend Started");
            return true;
        }
        else
        {
            this.fail("Unsupported PDNS Pipe ABI Version");
        }
        return false;
    }
    
    /**
     * Process commands from PDNS server
     */
    public void processCommands()
    {
        String[] command;
        while ((command = this.readCommand()) != null)
        {
            // Process the command
            switch (command[0])
            {
                case "Q":
                    this.query(command);
                    break;
                default: 
                    this.fail("Unsupported command: " + Arrays.toString(command));
                    break;
            }
        }
    }
    
    protected void query(String[] command)
    {
        if (command.length < 8)
        {
            this.fail("Got bad query from PDNS: " + Arrays.toString(command));
        }
        else
        {
            info("Got query: " + Arrays.toString(command));
            // Process the query
            //            0      1       2        3       4    5    6         7
            // command = ($type, $qname, $qclass, $qtype, $id, $ip, $localip, $ednsip)
            String qType = command[2];
            this.end();
        }
    }
    
    public void run()
    {
        if (this.hello())
        {
            this.processCommands();
        }
    }
    
    public static void main(String[] args)
    {
        new PDNSBackend(System.in, System.out, System.err).run();
    }
}
