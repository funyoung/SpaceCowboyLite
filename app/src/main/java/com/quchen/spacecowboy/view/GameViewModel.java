package com.quchen.spacecowboy.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;

import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.activity.Game;
import com.quchen.spacecowboy.sprite.BackGround;
import com.quchen.spacecowboy.sprite.Rocket;
import com.quchen.spacecowboy.sprite.cow.Cow;
import com.quchen.spacecowboy.sprite.cow.CowDance;
import com.quchen.spacecowboy.sprite.cow.CowFat;
import com.quchen.spacecowboy.sprite.cow.CowGhost;
import com.quchen.spacecowboy.sprite.cow.CowOld;
import com.quchen.spacecowboy.sprite.cow.CowZombie;
import com.quchen.spacecowboy.sprite.powerup.PowerUp;
import com.quchen.spacecowboy.sprite.powerup.PowerUpBell;
import com.quchen.spacecowboy.sprite.powerup.PowerUpMilkContainer;
import com.quchen.spacecowboy.sprite.powerup.PowerUpNitrokaese;
import com.quchen.spacecowboy.sprite.powerup.coin.Coin;
import com.quchen.spacecowboy.sprite.powerup.coin.Coin5;
import com.quchen.spacecowboy.sprite.rock.Rock;
import com.quchen.spacecowboy.sprite.rock.RockFat;
import com.quchen.spacecowboy.sprite.rock.RockFlash;
import com.quchen.spacecowboy.sprite.rock.RockFrozen;
import com.quchen.spacecowboy.sprite.rock.RockGuided;
import com.quchen.spacecowboy.utility.Util;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel {
    public static final byte POWER_ICE = 6; // speed / power
    public static final byte MAX_METEOR_SHOWER = 15;

    private final Achievement achievement = Achievement.getInstance();
    private final AccomplishmentsBox outbox = AccomplishmentsBox.instance();


    public volatile boolean punishment = false;    // Creates a meteor shower when the player sleeps for a certain amount of time
    private float NPCspeedModifier = 1;
    private Rocket rocket;
    private BackGround bg;
    private List<Rock> rocks;
    private List<Cow> cows;
    private List<PowerUp> powerUps;

    // todo: decouple the dependencies to GameView and Activity
    private final Activity activity;
    private final GameView view;
    public void showToast(int resId) {
        ((Game)activity).showToast(activity.getResources().getString(resId));
    }

    private void updateStatsText(final String text) {
        ((Game)activity).updateStatsText(text);
    }
    // todo: 待解耦

    private Context getContext() {
        return activity;
    }

    GameViewModel(GameView view, Activity activity) {
        this.view = view;
        this.activity = activity;

        achievement.setMilk((short) (Util.START_MILK + 2 * Option.getBoughItems(activity, Option.moreStartMilkSave)));

        rocket = new Rocket(view, activity, this);
        bg = new BackGround(view, activity, this);
        rocks = new ArrayList<>();
        cows = new ArrayList<>();
        powerUps = new ArrayList<>();
    }

    // todo: 把Canvas传给Sprite去绘制还是取Sprite数据出来绘
    void onDraw(Canvas canvas) {
        bg.draw(canvas);

        for (Rock r : this.rocks) {
            r.draw(canvas);
        }
        for (Cow c : this.cows) {
            c.draw(canvas);
        }
        for (PowerUp p : this.powerUps) {
            p.draw(canvas);
        }
        rocket.draw(canvas);
    }

    /**
     * Checks whether a gameobject is timed out
     * isTimedOut means collided and stuff
     */
    void checkTimeOut() {
        for (int i = 0; i < this.rocks.size(); i++) {
            if (this.rocks.get(i).isTimedOut()) {
                this.rocks.remove(i);
                i--;
            }
        }
        for (int i = 0; i < this.cows.size(); i++) {
            if (this.cows.get(i).isTimedOut()) {
                this.cows.remove(i);
                i--;
            }
        }
        for (int i = 0; i < this.powerUps.size(); i++) {
            if (this.powerUps.get(i).isTimedOut()) {
                this.powerUps.remove(i);
                i--;
            }
        }
    }

    /**
     * Checks whether sprites are out of range and deletes them
     */
    void checkOutOfRange() {
        for (int i = 0; i < this.rocks.size(); i++) {
            if (this.rocks.get(i).isOutOfRange()) {
                this.rocks.remove(i);
                i--;
            }
        }
        for (int i = 0; i < this.cows.size(); i++) {
            if (this.cows.get(i).isOutOfRange()) {
                this.cows.remove(i);
                i--;
            }
        }
        for (int i = 0; i < this.powerUps.size(); i++) {
            if (this.powerUps.get(i).isOutOfRange()) {
                this.powerUps.remove(i);
                i--;
            }
        }
    }

    /**
     * Checks collisions and performs the action (dmg, heal)
     */
    void checkCollision() {
        for (Rock r : this.rocks) {
            if (r.isColliding(this.rocket)) {
                r.onCollision();
            }
        }
        for (Cow c : this.cows) {
            if (c.isColliding(this.rocket)) {
                c.onCollision();
            }
        }

        for (PowerUp p : this.powerUps) {
            if (p.isColliding(this.rocket)) {
                p.onCollision();
            }
        }
    }

    /**
     * Can create new Gameobjects
     */
    private void createNew() {
        if (this.rocks.size() < (Util.lvl * 2 + Util.AMOUNT_OF_ROCKS)) {
            this.createNewRock();
        }

        if (this.cows.size() < (Util.lvl / 2 + Util.AMOUNT_OF_COWS)) {
            this.createNewCow();
        }

        if (this.powerUps.size() < (Util.lvl / 4 + Util.AMOUNT_OF_POWER_UPS)) {
            this.createNewPowerUp();
        }

        if (punishment) {
            punishment = false;
            createMeteorshower((short) 100);
        }
    }

    private void createNewRock() {
        int choice = (int) (Math.random() * 100);
        if ((choice -= Util.CHANCE_FROZEN_ROCK) < 0 && Util.lvl >= Util.START_LVL_FROZEN_ROCK) {
            rocks.add(new RockFrozen(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_FLASH_ROCK) < 0 && Util.lvl >= Util.START_LVL_FLASH_ROCK) {
            rocks.add(new RockFlash(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_GUIDED_ROCK) < 0 && Util.lvl >= Util.START_LVL_GUIDED_ROCK) {
            rocks.add(new RockGuided(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_FAT_ROCK) < 0 && Util.lvl >= Util.START_LVL_FAT_ROCK) {
            rocks.add(new RockFat(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_METEOR_SHOWER) < 0 && Util.lvl >= Util.START_LVL_METEOR_SHOWER) {
            createMeteorshower((short) (Math.random() * MAX_METEOR_SHOWER));
        } else {
            rocks.add(new Rock(view, getContext(), this));
        }
    }

    public void createNewRock(int x, int y) {
        createNewRock();
        this.rocks.get(this.rocks.size() - 1).setX(x);
        this.rocks.get(this.rocks.size() - 1).setY(y);
    }

    private void createNewCow() {
        int choice = (int) (Math.random() * 100);
        if ((choice -= Util.CHANCE_FAT_COW) < 0 && Util.lvl >= Util.START_LVL_FAT_COW) {
            cows.add(new CowFat(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_DANCE_COW) < 0 && Util.lvl >= Util.START_LVL_DANCE_COW) {
            cows.add(new CowDance(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_OLD_COW) < 0 && Util.lvl >= Util.START_LVL_OLD_COW) {
            cows.add(new CowOld(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_GHOST_COW) < 0 && Util.lvl >= Util.START_LVL_GHOST_COW) {
            cows.add(new CowGhost(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_ZOMBIE_COW) < 0 && Util.lvl >= Util.START_LVL_ZOMBIE_COW) {
            cows.add(new CowZombie(view, getContext(), this));
        } else {
            cows.add(new Cow(view, getContext(), this));
        }
    }

    private void createNewPowerUp() {
        int choice = (int) (Math.random() * 100);
        if ((choice -= Util.CHANCE_MILKCONTAINER) < 0 && Util.lvl >= Util.START_LVL_MILKCONTAINER) {
            powerUps.add(new PowerUpMilkContainer(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_NITROKAESE) < 0 && Util.lvl >= Util.START_LVL_NITROKAESE) {
            powerUps.add(new PowerUpNitrokaese(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_BELL) < 0 && Util.lvl >= Util.START_LVL_BELL) {
            powerUps.add(new PowerUpBell(view, getContext(), this));
        } else if ((choice -= Util.CHANCE_COIN5) < 0 && Util.lvl >= Util.START_LVL_COIN5) {
            powerUps.add(new Coin5(view, getContext(), this));
        } else {
            powerUps.add(new Coin(view, getContext(), this));
        }
    }

    public void createNewPowerUp(int x, int y) {
        createNewPowerUp();
        this.powerUps.get(this.powerUps.size() - 1).setX(x);
        this.powerUps.get(this.powerUps.size() - 1).setY(y);
    }

    /**
     * Update sprite movements
     */
    private void move() {
        bg.move(1);
        rocket.move(1);
        for (Rock r : this.rocks) {
            r.move(NPCspeedModifier);
        }
        for (Cow c : this.cows) {
            c.move(NPCspeedModifier);
        }
        for (PowerUp p : this.powerUps) {
            p.move(NPCspeedModifier);
        }
    }

    void checkTouchedRocks(int touchX, int touchY) {
        for (int i = 0; i < this.rocks.size(); i++) {
            if (this.rocks.get(i).isTouching(touchX, touchY)) {
                this.rocks.get(i).inflictDamage();
            }
        }
    }

    void onTiltChange(float x, float y) {
        if (this.rocket.isStunned()) {
            x = y = 0;
        } else {
            x -= Util.DefaultXAngle;
            y -= Util.DefaultYAngle;
            x *= Util.getSpeedFactor();
            y *= Util.getSpeedFactor();

            if (x < 4 && x > -4) {
                x = 0;
            }
            if (y < 4 && y > -4) {
                y = 0;
            }

            if (x >= 0) {
                x = (int) (0.5 * Math.pow(x, 1.3));
            } else {
                x *= -1;
                x = (int) (0.5 * Math.pow(x, 1.3));
                x *= -1;
            }
            if (y >= 0) {
                y = (int) (0.5 * Math.pow(y, 1.3));
            } else {
                y *= -1;
                y = (int) (0.5 * Math.pow(y, 1.3));
                y *= -1;
            }

            if (this.rocket.isFrozen()) {
                x /= POWER_ICE;
                y /= POWER_ICE;
            }
        }

        rocket.setSpeedX((int) x);
        rocket.setSpeedY((int) y);

        bg.setSpeedX((int) x);
        bg.setSpeedY((int) y);
    }
    public int getBGxSpeed() {
        return this.bg.speedX;
    }

    public int getBGySpeed() {
        return this.bg.speedY;
    }

    public Rocket getRocket() {
        return this.rocket;
    }

    public void setNPCSpeedModifier(float val) {
        this.NPCspeedModifier = val;
    }

    public void resetNPCSpeedModifier() {
        this.NPCspeedModifier = 1;
    }

    public void spawnCows(int amount) {
        for (int i = 0; i < amount; i++) {
            this.createNewCow();
        }
    }

    public void attractCows() {
        for (Cow c : this.cows) {
            c.moveTo(this.rocket.getX(), this.rocket.getY());
        }
    }

    private void createMeteorshower(short amount) {
        this.createNewRock();    //Random Rock
        int posX = this.rocks.get(this.rocks.size() - 1).getX();
        int posY = this.rocks.get(this.rocks.size() - 1).getY();
        double random = Math.random();
        int speedY = (int) (((this.rocket.y - posY) >> 5) * random);
        int speedX = (int) (((this.rocket.x - posX) >> 5) * random);
        this.rocks.get(this.rocks.size() - 1).setSpeedX(speedX);
        this.rocks.get(this.rocks.size() - 1).setSpeedY(speedY);

        for (int i = 1; i < amount; i++) {
            double speedMod = Math.random() + 0.5;
            if (speedMod > 1) {
                speedMod = 1;
            }
            this.rocks.add(new Rock(view, getContext(), this,
                    posX + (int) (Math.random() * Util.PIXEL_HEIGHT / 2 - Util.PIXEL_HEIGHT / 4),
                    posY + (int) (Math.random() * Util.PIXEL_HEIGHT / 2 - Util.PIXEL_HEIGHT / 4),
                    (int) (speedX * speedMod),
                    (int) (speedY * speedMod)));
        }

        showToast(R.string.ToastMeteorShower);
    }


    public void run() {
        checkTimeOut();
        checkOutOfRange();
        checkCollision();
        createNew();
        move();
    }

    public void punish() {
        punishment = true;
    }

    public void increasePoints(int value) {
        achievement.setLastTimeCoin(GameTimerTick.getElapsedTime());

        outbox.score += value;
        int tmp = Util.lvl;
        Util.lvl = (short) (outbox.score / Util.COINS_FOR_LEVELUP);
        if (tmp < Util.lvl) {
            showToast(R.string.level_up);
        }
    }

    public void killedMeteorid() {
        this.outbox.meteoroids++;
    }

    public void milkedCow() {
        this.outbox.cows++;
    }

    public void collectedPowerUp() {
        this.outbox.powerups++;
    }

    private void sleepyCowboy() {
        showToast(R.string.sleepy_hint);
        view.punish();
        achievement.setLastTimeCoin(GameTimerTick.getElapsedTime());
    }

    public void updateStatsText() {
        updateStatsText("\t\t" + achievement.getStatusText()
                + "\t\t\t\t\t\t" + "LVL: " + Util.lvl
                + "\t\t\t\t\t\t" + "Coins: " + AccomplishmentsBox.instance().score);

    }
}
