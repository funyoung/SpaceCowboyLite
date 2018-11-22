package com.quchen.spacecowboy.view;
/**
 * Most important class of the game.
 * Hold the game loop which checks all objects and draws them.
 * Gets the tiltevents.
 *
 * @author lars
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.quchen.spacecowboy.activity.Game;
import com.quchen.spacecowboy.utility.Tiltable;
import com.quchen.spacecowboy.utility.TimerExec;
import com.quchen.spacecowboy.utility.Util;

public class GameView extends SurfaceView implements Runnable, Tiltable {
    private Game game;
    private Thread t;
    private SurfaceHolder holder;

    volatile private boolean shouldRun = true;
    volatile private int touchX, touchY;
    volatile private boolean isTouched = false;

    // todo: 把Model从对View和activity的依赖中解耦出来
    private GameViewModel viewModel;

    /** Not usable */
    private GameView(Context context) {
        super(context);
    }

    /** Not usable */
    private GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Not usable */
    private GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param activity the game, class:Game
     */
    public GameView(Activity activity) {
        super(activity);
        this.game = (Game) activity;
        this.shouldRun = true;
        holder = getHolder();

        viewModel = new GameViewModel(this, activity);
    }

    @SuppressLint("WrongCall")
    public void run() {
        while (shouldRun) {
            if (!holder.getSurface().isValid()) {
                continue;
            }

            // checks
            if (Achievement.getInstance().getMilk() <= 0) {
                gameOver();
            }
            if (this.isTouched) {
                viewModel.checkTouchedRocks(touchX, touchY);
                this.isTouched = false;
            }

            viewModel.run();

            // draw
            Canvas c = holder.lockCanvas();
            onDraw(c);

            holder.unlockCanvasAndPost(c);
            viewModel.updateStatsText();

            // sleep
            try {
                Thread.sleep(Util.UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return shouldRun;
    }

    public void pause() {
        TimerExec.onPause();

        shouldRun = false;
        while (t != null) {
            try {
                t.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t = null;
    }

    public void resume() {
        shouldRun = false;
        while (t != null) {
            try {
                t.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        shouldRun = true;
        t = new Thread(this);
        t.start();

        TimerExec.onResume();
    }

    /**
     * Draws the background and all sprites
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewModel.onDraw(canvas);
    }

    private void gameOver() {
        if (AccomplishmentsBox.instance().score == 1337) {
            AccomplishmentsBox.instance().leet_king = true;
        }

        // todo: 没有此Activity了。
        Intent intent = new Intent("com.quchen.spacecowboy.activity.AddScore");
        intent.putExtra("points", AccomplishmentsBox.instance().score);
        this.game.startActivity(intent);
        this.game.finish();
        this.shouldRun = false;
    }

    public void Touched(int x, int y) {
        this.touchX = x;
        this.touchY = y;
        this.isTouched = true;
    }

    public void onTiltChange(float x, float y) {
        viewModel.onTiltChange(x, y);
    }

    public void punish() {
        viewModel.punish();
    }
}
