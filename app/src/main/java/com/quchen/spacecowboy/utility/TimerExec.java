package com.quchen.spacecowboy.utility;
/**
 * A Timer that can pause and resume
 * All timers are stored in a list so they can be paused and resume at the ~same time
 * https://github.com/c05mic/pause-resume-timer
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerExec {
    private static List<TimerExec> allTimers = new ArrayList<>();
    private volatile boolean isRunning;
    private volatile boolean isPaused;
    private volatile long interval;
    private long elapsedTime;
    private volatile long duration;
    private final ScheduledExecutorService execService = Executors.newSingleThreadScheduledExecutor();
    private Future<?> future;
    private TimerExecTask task;

    public TimerExec() {
        this(1000, -1, new TimerExecTask() {
            @Override
            public void onTick() {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * @param interval
     * @param duration -1 = infinity
     * @param task
     */
    public TimerExec(long interval, long duration, TimerExecTask task) {
        this.interval = interval;
        this.duration = duration;
        this.task = task;
        elapsedTime = 0;

        TimerExec.allTimers.add(this);
    }


    public static void stopAllTimers() {
        for (TimerExec t : TimerExec.allTimers) {
            t.cancel();
        }
        TimerExec.allTimers.clear();
    }

    public static void onPause() {
        for (TimerExec timer : allTimers) {
            timer.pause();
        }
    }

    public static void onResume() {
        for (TimerExec timer : allTimers) {
            timer.resume();
        }
    }

    public void start() {
        if (this.isRunning) {
            return;
        }
        this.isRunning = true;
        this.isPaused = false;
        this.future = this.execService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                TimerExec.this.task.onTick();
                TimerExec.this.elapsedTime += TimerExec.this.interval;
                if (TimerExec.this.duration > 0) {    // -1 = infinity
                    if (TimerExec.this.elapsedTime >= TimerExec.this.duration) {
                        TimerExec.this.future.cancel(false);
                        TimerExec.this.task.onFinish();
                    }
                }
            }

        }, this.interval, this.interval, TimeUnit.MILLISECONDS);
    }

    public void pause() {
        if (!this.isRunning) {
            return;
        }
        this.future.cancel(false);
        this.isRunning = false;
        this.isPaused = true;
    }

    public void resume() {
        if (!this.isPaused) {
            return;
        }
        this.start();
    }

    public void cancel() {
        this.pause();
        this.elapsedTime = 0;
        this.isPaused = false;
    }

    public void setTimer(TimerExecTask task, long interval, long duration) {
        this.interval = interval;
        this.duration = duration;
        this.task = task;
        elapsedTime = 0;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        TimerExec.allTimers.remove(this);
    }

}

