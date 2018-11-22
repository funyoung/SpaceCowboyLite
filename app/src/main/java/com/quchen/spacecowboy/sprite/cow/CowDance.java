package com.quchen.spacecowboy.sprite.cow;
/**
 * Cow that flees from the rocket
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.view.AccomplishmentsBox;
import com.quchen.spacecowboy.view.Achievement;
import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.utility.Util;
import com.quchen.spacecowboy.view.GameViewModel;

public class CowDance extends Cow {
    public static final int COW_TYPE = 1;
    public static final byte POWER_DANCE_COW = 2;
    public static final short ANIMATION_TIME = 150;
    public static final byte NUMBER_OF_ROWS = 1;
    public static final byte NUMBER_OF_COLUMNS = 4;

    protected static Bitmap globalBitmap;

    public CowDance(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.dance_cow));
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight();
        this.power = POWER_DANCE_COW;
        this.frameTime = ANIMATION_TIME / Util.UPDATE_INTERVAL;
        this.colNr = 4;
    }

    @Override
    public void move(float speedModifier) {
        super.move(speedModifier);
        this.speedX = viewModel.getRocket().getSpeedX() / 2;
        this.speedY = viewModel.getRocket().getSpeedY() / 2;
    }

    @Override
    public void onCollision() {
        super.onCollision();
        if (viewModel.getRocket().isFrozen()) {
            Achievement.getInstance().catchedDancecowFrozen();
        }
        AccomplishmentsBox.instance().catch_them_all |= (1 << COW_TYPE);
    }


}
