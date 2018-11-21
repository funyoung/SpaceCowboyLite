package com.quchen.spacecowboy.sprite;
/**
 * Displays the status of the player
 * Poisoned, stuned, frozen
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.quchen.spacecowboy.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.utility.Util;

public class Status extends Sprite {
    public static final byte NUMBER_OF_ROWS = 3;
    public static final byte NUMBER_OF_COLUMNS = 4;
    public static final short ANIMATION_TIME = 100;

    protected static Bitmap globalBitmap;

    public Status(GameView view, Context context) {
        super(view, context);
        if (globalBitmap == null) {
            globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.status));
        }
        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight() / NUMBER_OF_ROWS;
        this.frameTime = ANIMATION_TIME / Util.UPDATE_INTERVAL;
        this.colNr = 4;
    }


}
