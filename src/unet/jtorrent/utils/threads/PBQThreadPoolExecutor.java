package unet.jtorrent.utils.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PBQThreadPoolExecutor extends ThreadPoolExecutor {

    public PBQThreadPoolExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit unit,
                                 PriorityBlockingQueue<Runnable> workQueue){
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public PBQThreadPoolExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit unit,
                                 PriorityBlockingQueue<Runnable> workQueue,
                                 ThreadFactory threadFactory){
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public PBQThreadPoolExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit unit,
                                 PriorityBlockingQueue<Runnable> workQueue,
                                 RejectedExecutionHandler handler){
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result){
        if(task == null){
            throw new NullPointerException();
        }
        RunnableFuture<T> ftask = newTaskFor(task, result, 0);
        execute(ftask);
        return ftask;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task){
        if(task == null){
            throw new NullPointerException();
        }
        RunnableFuture<T> ftask = newTaskFor(task, 0);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Runnable task, T result, int priority) {
        if(task == null){
            throw new NullPointerException();
        }
        RunnableFuture<T> ftask = newTaskFor(task, result, priority);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task, int priority){
        if(task == null){
            throw new NullPointerException();
        }
        RunnableFuture<T> ftask = newTaskFor(task, priority);
        execute(ftask);
        return ftask;
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value, int priority){
        return new ComparableFutureTask<T>(runnable, value, priority);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable, int priority){
        return new ComparableFutureTask<T>(callable, priority);
    }
}
