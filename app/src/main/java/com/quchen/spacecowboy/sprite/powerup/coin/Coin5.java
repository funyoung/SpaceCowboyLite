package com.quchen.spacecowboy.sprite.powerup.coin;
/**
 * Coin that give 5 coins
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.view.Achievement;
import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.view.GameViewModel;

public class Coin5 extends Coin {
    public static final byte NUMBER_OF_ROWS = 1;
    public static final byte NUMBER_OF_COLUMNS = 12;
    protected static Bitmap globalBitmap;

    public Coin5(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.coin5));
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight();
    }

    @Override
    public void onCollision() {
        super.onCollision();
        viewModel.increasePoints(5 - 1);
        Achievement.getInstance().redCoinCollected();
    }
}
