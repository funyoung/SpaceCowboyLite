package com.quchen.spacecowboy.sprite.powerup;
/**
 * A cheese that slows down everything except the player himself
 * Cheese = Kaese (german)
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.utility.TimerExec;
import com.quchen.spacecowboy.utility.TimerExecTask;
import com.quchen.spacecowboy.utility.Util;
import com.quchen.spacecowboy.view.GameViewModel;

public class PowerUpNitrokaese extends PowerUp {
    public static final int TIME_NITRO = 5000;
    public static final byte NUMBER_OF_ROWS = 1;
    public static final byte NUMBER_OF_COLUMNS = 1;
    protected static Bitmap globalBitmap;
    static private TimerExec nitroTimer;

    public PowerUpNitrokaese(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.nitrokaese));
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
        nitroTimer = new TimerExec();
    }

    @Override
    public void onCollision() {
        super.onCollision();
        viewModel.setNPCSpeedModifier(0.1f);
        viewModel.showToast((R.string.ToastNitroOn));

        nitroTimer.cancel();
        nitroTimer.setTimer(new TimerExecTask() {
            @Override
            public void onFinish() {
                viewModel.resetNPCSpeedModifier();
                viewModel.showToast((R.string.ToastNitroOff));
            }

            @Override
            public void onTick() {
            }
        }, Util.TIME_DEFAULT_TICK, TIME_NITRO);
        nitroTimer.start();
    }

}
