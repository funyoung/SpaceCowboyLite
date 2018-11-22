package com.quchen.spacecowboy.sprite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.view.GameViewModel;

/**
 * Background
 *
 * @author lars
 */
public class BackGround extends Sprite {

    public BackGround(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bg1);
        this.bitmap = bd.getBitmap();
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
    }

    @Override
    public void move(float speedModifier) {
        this.x = this.x - this.speedX;
        if (this.x > 0) {
            this.x -= this.bitmap.getWidth();
        }
        this.y = this.y - this.speedY;
        if (this.y > 0) {
            this.y -= this.bitmap.getHeight();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; (x + bitmap.getWidth() * i) < canvas.getWidth(); i++) {
            for (int j = 0; (y + bitmap.getHeight() * j) < canvas.getHeight(); j++) {
                Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                Rect dst = new Rect(x + bitmap.getWidth() * i,
                        y + bitmap.getHeight() * j,
                        x + bitmap.getWidth() * i + bitmap.getWidth(),
                        y + bitmap.getHeight() * j + bitmap.getHeight());
                canvas.drawBitmap(this.bitmap, src, dst, null);
            }
        }
    }

}
