package com.quchen.spacecowboy.sprite.powerup;
/**
 * Class that all powerups inherit from
 *
 * @author lars
 */

import android.content.Context;

import com.quchen.spacecowboy.GameView;
import com.quchen.spacecowboy.sprite.Sprite;
import com.quchen.spacecowboy.utility.Util;

public abstract class PowerUp extends Sprite {

    public PowerUp(GameView view, Context context) {
        super(view, context);
    }

    @Override
    public void onCollision() {
        super.onCollision();
        this.isTimedOut = true;
        if (!(this instanceof Coin)) {
            this.view.getGame().collectedPowerUp();
        }
    }

    @Override
    public boolean isColliding(Sprite sprite) {
        return isColliding(sprite, Util.COIN_COLLISION_FACTOR);
    }
}
