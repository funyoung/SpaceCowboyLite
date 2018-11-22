package com.quchen.spacecowboy.view;

import com.quchen.spacecowboy.utility.TimerExec;
import com.quchen.spacecowboy.utility.TimerExecTask;

import java.util.ArrayList;
import java.util.List;

public class GameTimerTick {
    private final static long gameTimerTick = 1000;

    private final List<OnTickListener> onTickListeners = new ArrayList<>();

    private GameTimerTick(){}

    private static GameTimerTick instance;
    private static GameTimerTick get() {
        if (null == instance) {
            instance = new GameTimerTick();
        }
        return instance;
    }

    public TimerExec gameTimer = new TimerExec(gameTimerTick, -1, new TimerExecTask() {
        @Override
        public void onTick() {
            for (OnTickListener listener : onTickListeners) {
                listener.onTick();
            }
        }

        @Override
        public void onFinish() {
        }
    });

    public static void start() {
        get().gameTimer.start();
    }

    public static void addOnTickListener(OnTickListener listener) {
        get().onTickListeners.add(listener);
    }

    public static long getElapsedTime() {
        return get().gameTimer.getElapsedTime();
    }

    public interface OnTickListener {
        void onTick();
    }
}
