package com.quchen.spacecowboy.sprite;
/**
 * The player
 *
 * @author lars
 */

import android.content.Context;
import android.graphics.Canvas;

import com.quchen.spacecowboy.view.Achievement;
import com.quchen.spacecowboy.view.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.utility.TimerExec;
import com.quchen.spacecowboy.utility.TimerExecTask;
import com.quchen.spacecowboy.utility.Util;
import com.quchen.spacecowboy.view.GameViewModel;

public class Rocket extends Sprite {
    public static final int TIME_SHIELD = 5000;
    public static final int TIME_POISON = 6000;
    public static final int TIME_POISON_TICK = 1500;
    public static final int TIME_FREEZE = 4000;
    public static final int TIME_STUN = 1500;

    public static final byte NUMBER_OF_ROWS = 8;
    public static final byte NUMBER_OF_COLUMNS = 3;
    public static final byte START_FLAME = (byte) (10 * Util.getScaleFactor());
    public static final byte START_BIG_FLAME = (byte) (20 * Util.getScaleFactor());
    public static final byte POISON = 0;
    public static final byte FROST = 1;
    public static final byte STUN = 2;
    public static final byte SHIELD = 4;

    private TimerExec statusTimer;
    private int statusType = -1;
    private boolean isShieldOn = false;
//    private long lastTimeDamaged = 0;
    private int meteoroidsDestroyedWithShield = 0;

    private Shield shield;
    private Status status;
    private Damage damage;

    public Rocket(GameView view, Context context, GameViewModel viewModel) {
        super(view, context, viewModel);

        this.bitmap = createBitmap(context.getResources().getDrawable(R.drawable.rocket));
        this.shield = new Shield(view, context, viewModel);
        this.status = new Status(view, context, viewModel);
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight() / NUMBER_OF_ROWS;
        this.statusTimer = new TimerExec();
    }

    @Override
    public void move(float speedModifier) {
        int angle = getAngle(this.speedX, this.speedY);
        row = (byte) (((angle + 22) / 45) % 8);

        moveStatus(speedModifier);

        if (this.damage != null) {
            this.damage.move(speedModifier);
            if (this.damage.isTimedOut()) {
                this.damage = null;
            }
        }

        int power = (int) Math.sqrt(this.speedX * this.speedX + this.speedY * this.speedY);
        if (Math.abs(power) <= START_FLAME) {
            col = 0;
        } else if (Math.abs(power) <= START_BIG_FLAME) {
            col = 1;
        } else {
            col = 2;
        }
    }

    private void moveStatus(float speedModifier) {
        if (this.isShieldOn) {
            this.shield.move(speedModifier);
        }
        if (this.statusType >= 0) {
            this.status.move(speedModifier);
        }
    }

    private void clearStatus() {
        this.statusType = -1;
    }

    @Override
    public void draw(Canvas canvas) {
        this.x = (canvas.getWidth() >> 1) - (this.width >> 1);
        this.y = (canvas.getHeight() >> 1) - (this.height >> 1);
        super.draw(canvas);

        if (this.isShieldOn) {
            this.shield.setX(this.x);
            this.shield.setY(this.y);
            this.shield.draw(canvas);
        }
        if (this.statusType >= 0) {
            this.status.setX(this.x);
            this.status.setY(this.y);
            this.status.draw(canvas);
        }
        if (this.damage != null) {
            this.damage.setX(this.x);
            this.damage.setY(this.y);
            this.damage.draw(canvas);
        }
    }

    private int getAngle(int x, int y) {
        int angle = 0;
        angle = (int) Math.toDegrees(Math.atan2(x, -y));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public boolean isFrozen() {
        return this.statusType == FROST;
    }

    public boolean isStunned() {
        return this.statusType == STUN;
    }

    public boolean isPoisoned() {
        return this.statusType == POISON;
    }

    public void inflictDamage(int value) {
        if (!this.isShieldOn) {
            Achievement.getInstance().decreaseMilk(value);
//            lastTimeDamaged = GameTimerTick.getElapsedTime();
            if (this.damage == null && !this.isPoisoned()) {
                this.damage = new Damage(view, context, viewModel);
            }
        } else {
            this.meteoroidsDestroyedWithShield++;
            if (meteoroidsDestroyedWithShield >= 10) {
                Achievement.getInstance().destroyed10MeteoroidsWitchShield();
            }
        }
    }

    public void activateShield() {
        if (this.statusType == POISON) {
            Achievement.getInstance().healedPoison();
        }
        this.clearStatus();
        this.isShieldOn = true;
        viewModel.showToast(R.string.ToastShield);

        this.statusTimer.cancel();
        this.statusTimer.setTimer(new TimerExecTask() {
            @Override
            public void onTick() {
            }

            @Override
            public void onFinish() {
                isShieldOn = false;
                meteoroidsDestroyedWithShield = 0;
            }
        }, Util.TIME_DEFAULT_TICK, TIME_SHIELD);
        this.statusTimer.start();
    }

    public void inflictPoison(final short damagePerTick) {
        if (!this.isShieldOn) {
            viewModel.showToast(R.string.ToastPoison);
            this.clearStatus();
            this.statusType = POISON;
            this.status.row = POISON;

            this.statusTimer.cancel();
            this.statusTimer.setTimer(new TimerExecTask() {
                @Override
                public void onTick() {
                    inflictDamage(damagePerTick);
                }

                @Override
                public void onFinish() {
                    statusType = -1;
                }
            }, TIME_POISON_TICK, (int) (TIME_POISON * Util.STATUS_EFFECT_FACTOR));
            this.statusTimer.start();
        }
    }

    public void inflictIce() {
        if (!this.isShieldOn) {
            viewModel.showToast(R.string.ToastFrost);
            this.clearStatus();
            this.statusType = FROST;
            this.status.row = FROST;

            this.statusTimer.cancel();
            this.statusTimer.setTimer(new TimerExecTask() {
                @Override
                public void onTick() {
                }

                @Override
                public void onFinish() {
                    statusType = -1;
                }
            }, Util.TIME_DEFAULT_TICK, (int) (TIME_FREEZE * Util.STATUS_EFFECT_FACTOR));
            this.statusTimer.start();
        }
    }

    public void inflictStun() {
        if (!this.isShieldOn) {
            viewModel.showToast((R.string.ToastFlash));
            this.clearStatus();
            this.statusType = STUN;
            this.status.row = STUN;

            this.statusTimer.cancel();
            this.statusTimer.setTimer(new TimerExecTask() {
                @Override
                public void onTick() {
                }

                @Override
                public void onFinish() {
                    statusType = -1;
                }
            }, Util.TIME_DEFAULT_TICK, (int) (TIME_STUN * Util.STATUS_EFFECT_FACTOR));
            this.statusTimer.start();
        }
    }

//    public long getLastTimeDamaged() {
//        return lastTimeDamaged;
//    }
}
