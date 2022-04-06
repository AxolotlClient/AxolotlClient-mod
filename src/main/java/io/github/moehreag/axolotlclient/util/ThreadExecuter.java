package io.github.moehreag.axolotlclient.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadExecuter {

    private static final ScheduledExecutorService EXECUTER_SERVICE = Executors.newScheduledThreadPool(2);

    public static void scheduleTask(Runnable runnable){
        scheduleTask(runnable, 0, TimeUnit.MILLISECONDS);
    }

    public static void scheduleTask(Runnable runnable, long delay, TimeUnit timeUnit){
        EXECUTER_SERVICE.schedule(runnable, delay, timeUnit);
    }

}
