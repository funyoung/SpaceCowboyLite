package com.quchen.spacecowboy.sprite.cow;
/**
 * Cow that doesn't give milk but a protecting shield
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.view.AccomplishmentsBox;
import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.utility.Util;
import com.quchen.spacecowboy.view.GameViewModel;

public class CowGhost extends Cow {
    public static final int COW_TYPE = 4;
    public static final byte POWER_GHOST_COW = 0;
    public static final short ANIMATION_TIME = 150;
    public static final byte NUMBER_OF_ROWS = 1;
    public static final byte NUMBER_OF_COLUMNS = 4;

    protected static Bitmap globalBitmap;

    public CowGhost(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.ghost_cow));
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight();
        this.power = POWER_GHOST_COW;
        this.colNr = 4;
        this.frameTime = ANIMATION_TIME / Util.UPDATE_INTERVAL;
    }

    @Override
    public void onCollision() {
        super.onCollision();
        viewModel.getRocket().activateShield();
        AccomplishmentsBox.instance().catch_them_all |= (1 << COW_TYPE);
    }

    @Override
    public void setSpeedX(int speedX) {
        super.setSpeedX(speedX);
        if (speedX < 0) {
            this.bitmap = globalBitmapMirror;
        } else {
            this.bitmap = globalBitmap;
        }
    }

}
