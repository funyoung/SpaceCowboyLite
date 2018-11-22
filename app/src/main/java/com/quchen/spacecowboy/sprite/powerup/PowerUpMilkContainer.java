package com.quchen.spacecowboy.sprite.powerup;
/**
 * A Milkcontainer that raises the max milk amount
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.view.Achievement;
import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.view.GameViewModel;

public class PowerUpMilkContainer extends PowerUp {
    public static final byte MILK_CONTAINER_MAX_INCREASE = 5;
    public static final byte MILK_IN_CONTAINER = 2;
    public static final byte NUMBER_OF_ROWS = 1;
    public static final byte NUMBER_OF_COLUMNS = 1;
    protected static Bitmap globalBitmap;

    public PowerUpMilkContainer(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.milk));
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
        this.speedX = this.speedX >> 2;
        this.speedY = this.speedY >> 2;
    }

    @Override
    public void onCollision() {
        super.onCollision();
        Achievement.getInstance().increaseMilkMax(MILK_CONTAINER_MAX_INCREASE);
        Achievement.getInstance().increaseMilk(MILK_IN_CONTAINER);
    }
}
