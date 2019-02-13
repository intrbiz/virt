package com.intrbiz.vpp.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class FutureMapping<T, R> implements Future<R>
{
    private final Future<T> future;
    
    private final Function<T, R> mapper;

    public FutureMapping(Future<T> future, Function<T, R> mapper)
    {
        super();
        this.future = future;
        this.mapper = mapper;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return this.future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled()
    {
        return this.future.isCancelled();
    }

    @Override
    public boolean isDone()
    {
        return this.future.isDone();
    }

    @Override
    public R get() throws InterruptedException, ExecutionException
    {
        return this.mapper.apply(this.future.get());
    }

    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        return this.mapper.apply(this.future.get(timeout, unit));
    }
}
