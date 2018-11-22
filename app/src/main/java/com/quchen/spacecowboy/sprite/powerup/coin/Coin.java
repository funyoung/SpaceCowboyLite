package com.quchen.spacecowboy.sprite.powerup.coin;
/**
 * Coin
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.sprite.powerup.PowerUp;
import com.quchen.spacecowboy.utility.Util;
import com.quchen.spacecowboy.view.GameViewModel;

public class Coin extends PowerUp {
    public static final short ANIMATION_TIME = 100;
    public static final byte NUMBER_OF_ROWS = 1;
    public static final byte NUMBER_OF_COLUMNS = 12;

    protected static Bitmap globalBitmap;
    protected static int sound = -1;

    public Coin(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.coin));
        }
        if (sound == -1) {
            sound = Util.soundPool.load(context, R.raw.coin, 1);
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight();
        this.speedX = 0;
        this.speedY = 0;
        this.frameTime = ANIMATION_TIME / Util.UPDATE_INTERVAL;
        this.colNr = 12;
    }

    @Override
    public void onCollision() {
        super.onCollision();
        viewModel.increasePoints(1);
    }

    @Override
    protected void playSound() {
        super.playSound();
        Util.soundPool.play(sound, Util.soundVolume, Util.soundVolume, 0, 0, 1);
    }

}
