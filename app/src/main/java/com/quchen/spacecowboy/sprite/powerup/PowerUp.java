package com.quchen.spacecowboy.sprite.powerup;
/**
 * Class that all powerups inherit from
 *
 * @author lars
 */

import android.content.Context;

import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.sprite.Sprite;
import com.quchen.spacecowboy.sprite.powerup.coin.Coin;
import com.quchen.spacecowboy.utility.Util;
import com.quchen.spacecowboy.view.GameViewModel;

public abstract class PowerUp extends Sprite {

    public PowerUp(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);
    }

    @Override
    public void onCollision() {
        super.onCollision();
        this.isTimedOut = true;
        if (!(this instanceof Coin)) {
            viewModel.collectedPowerUp();
        }
    }

    @Override
    public boolean isColliding(Sprite sprite) {
        return isColliding(sprite, Util.COIN_COLLISION_FACTOR);
    }
}
