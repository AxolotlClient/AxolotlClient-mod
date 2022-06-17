package io.github.axolotlclient.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadExecuter {

    private static final ScheduledExecutorService EXECUTER_SERVICE = new ScheduledThreadPoolExecutor(3, new ThreadFactoryBuilder().setNameFormat("ExecutionService Thread #%d").setDaemon(true).build());

    public static void scheduleTask(Runnable runnable){
        scheduleTask(runnable, 0, TimeUnit.MILLISECONDS);
    }

    public static void scheduleTask(Runnable runnable, long delay, TimeUnit timeUnit){
        EXECUTER_SERVICE.schedule(runnable, delay, timeUnit);
    }

}
