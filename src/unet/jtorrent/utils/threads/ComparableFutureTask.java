package unet.jtorrent.utils.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ComparableFutureTask<V> extends FutureTask<V> implements
        Runnable, Comparable<ComparableFutureTask<V>> {

    private int priority;

    public ComparableFutureTask(Callable<V> callable, int priority){
        super(callable);
        this.priority = priority;
    }

    public ComparableFutureTask(Runnable runnable, V result, int priority){
        super(runnable, result);
        this.priority = priority;
    }

    @Override
    public int compareTo(ComparableFutureTask<V> o){
        return this.priority - o.priority;
    }
}
