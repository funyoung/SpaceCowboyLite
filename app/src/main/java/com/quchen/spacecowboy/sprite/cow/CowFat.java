package com.quchen.spacecowboy.sprite.cow;
/**
 * Cow that doesn't move and disappears after a certain timeout
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.view.AccomplishmentsBox;
import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.utility.TimerExec;
import com.quchen.spacecowboy.utility.TimerExecTask;
import com.quchen.spacecowboy.utility.Util;
import com.quchen.spacecowboy.view.GameViewModel;

public class CowFat extends Cow {
    public static final int COW_TYPE = 3;
    public static final int TIME_FAT_COW = 8000;
    public static final byte POWER_FAT_COW = 3;
    public static final byte NUMBER_OF_ROWS = 1;
    public static final byte NUMBER_OF_COLUMNS = 1;

    protected static Bitmap globalBitmap;
    private TimerExec fadeOutTimer;

    public CowFat(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.fat_cow));
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
        this.power = POWER_FAT_COW;
        this.speedX = 0;
        this.speedY = 0;
        this.colNr = 1;
        this.fadeOutTimer = new TimerExec();
        this.fadeOutTimer.setTimer(new TimerExecTask() {
            @Override
            public void onTick() {
            }

            @Override
            public void onFinish() {
                isTimedOut = true;
            }
        }, Util.TIME_DEFAULT_TICK, TIME_FAT_COW);
        fadeOutTimer.start();
    }

    @Override
    public void onCollision() {
        super.onCollision();
        AccomplishmentsBox.instance().catch_them_all |= (1 << COW_TYPE);
    }

}
