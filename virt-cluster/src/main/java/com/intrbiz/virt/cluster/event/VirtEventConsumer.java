package com.intrbiz.virt.cluster.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hazelcast.core.IQueue;
import com.intrbiz.virt.event.VirtEvent;

public class VirtEventConsumer<T extends VirtEvent>
{
    private static final Logger logger = Logger.getLogger(VirtEventConsumer.class);
    
    private final IQueue<T> eventQueue;

    private final List<VirtEventHandler<T>> handlers = new CopyOnWriteArrayList<VirtEventHandler<T>>();
    
    private Thread consumerThread;
    
    private volatile boolean run = false;

    public VirtEventConsumer(IQueue<T> eventQueue)
    {
        super();
        this.eventQueue = eventQueue;
    }
    
    public void start()
    {
        this.consumerThread = new Thread(this::consumeLocalEventQueue);
        this.run = true;
        this.consumerThread.start();
    }
    
    public void shutdown()
    {
        this.run = false;
        try
        {
            this.consumerThread.join();
        }
        catch (InterruptedException e)
        {
        }
        this.handlers.clear();
    }
    
    public void addLocalEventHandler(VirtEventHandler<T> handler)
    {
        this.handlers.add(handler);
    }
    
    private void consumeLocalEventQueue()
    {
        while (this.run)
        {
            try
            {
                T event = this.eventQueue.poll(1L, TimeUnit.SECONDS);
                if (event != null)
                {
                    for (VirtEventHandler<T> handler : this.handlers)
                    {
                        handler.process(event);
                    }
                }
            }
            catch (InterruptedException e)
            {
            }
            catch (Exception e)
            {
                logger.warn("Failed to process host event", e);
            }
        }
    }
}
