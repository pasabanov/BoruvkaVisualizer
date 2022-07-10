package com.leti.summer_practice.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SingleTaskTimer extends Timer {


    public static final long NO_TASK_COMPLETED = -1;


    private long lastCompletedTaskTime = NO_TASK_COMPLETED; // nanoseconds


    private TimerTask currentTimerTask = null;


    public SingleTaskTimer() {

    }


    @Override
    public void schedule(TimerTask task, long delay) {
        cancel();
        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
                lastCompletedTaskTime = System.nanoTime();
            }
        };
        super.schedule(currentTimerTask, delay);
    }
    @Override
    public void schedule(TimerTask task, Date time) {
        cancel();
        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
                lastCompletedTaskTime = System.nanoTime();
            }
        };
        super.schedule(currentTimerTask, time);
    }
    @Override
    public void schedule(TimerTask task, long delay, long period) {
        cancel();
        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
                lastCompletedTaskTime = System.nanoTime();
            }
        };
        super.schedule(currentTimerTask, delay, period);
    }
    @Override
    public void schedule(TimerTask task, Date firstTime, long period) {
        cancel();
        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
                lastCompletedTaskTime = System.nanoTime();
            }
        };
        super.schedule(currentTimerTask, firstTime, period);
    }
    @Override
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        cancel();
        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
                lastCompletedTaskTime = System.nanoTime();
            }
        };
        super.scheduleAtFixedRate(currentTimerTask, delay, period);
    }
    @Override
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        cancel();
        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
                lastCompletedTaskTime = System.nanoTime();
            }
        };
        super.scheduleAtFixedRate(currentTimerTask, firstTime, period);
    }


    @Override
    public void cancel() {
        if (currentTimerTask != null)
            currentTimerTask.cancel();
        currentTimerTask = null;
    }


    public long timeFromLastCompletion() {
        return System.nanoTime() - lastCompletedTaskTime;
    }


    public TimerTask getCurrentTimerTask() {
        return currentTimerTask;
    }

    public boolean hasCurrentTimerTask() {
        return currentTimerTask != null;
    }
}