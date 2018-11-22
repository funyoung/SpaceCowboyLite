package com.quchen.spacecowboy.view;

import com.quchen.spacecowboy.utility.Util;

public class Achievement {
    private volatile short milk = Util.START_MILK;
    private volatile short milkContainer = Util.START_CONTAINER;

    //achievments
    private boolean catchedDancecowFrozen;
    private boolean healedPoison;
    private boolean destroyed10MeteoroidsWitchShield;
    private boolean redCoinCollected;
    private long lastTimeCoin;
    private long lowMilkTime = -1;
    private long fullMilkTime = -1;

    private static Achievement instance;
    public static Achievement getInstance() {
        if (null == instance) {
            instance = new Achievement();
        }

        return instance;
    }

    private Achievement() {}

    public void catchedDancecowFrozen() {
        this.catchedDancecowFrozen = true;
    }

    public void healedPoison() {
        this.healedPoison = true;
    }

    public void destroyed10MeteoroidsWitchShield() {
        this.destroyed10MeteoroidsWitchShield = true;
    }

    public void redCoinCollected() {
        this.redCoinCollected = true;
    }

    public void setLastTimeCoin(long elapsedTime) {
        lastTimeCoin = elapsedTime;
    }

    public void clearLowMilkTime() {
        this.lowMilkTime = -1;
    }

    public void setFullMilkTime(long elapsedTime) {
        this.fullMilkTime = elapsedTime;
    }

    public void setLowMilkTime(long elapsedTime) {
        this.lowMilkTime = elapsedTime;
    }

    public void clearFullMilkTime() {
        this.fullMilkTime = -1;
    }


    public static void setMilk(short milk) {
        milk = milk;
    }

    public int getMilk() {
        return milk;
    }

    public void increaseMilk(int milk) {
        int oldMilk = this.milk;
        this.milk += milk;
        if (this.milk > this.milkContainer) {
            this.milk = this.milkContainer;
        }

        if (this.milk != 1) {
            clearLowMilkTime();
        }

        if (this.milk == this.milkContainer && oldMilk != this.milkContainer) {
            setFullMilkTime(GameTimerTick.getElapsedTime());
        }
    }

    public void decreaseMilk(int milk) {
        this.milk -= milk;

        if (this.milk == 1 && milk != 0) {
            setLowMilkTime(GameTimerTick.getElapsedTime());
        }
        if (this.milk != this.milkContainer) {
            clearFullMilkTime();
        }
    }

    public int getMilkMax() {
        return milkContainer;
    }

    public void increaseMilkMax(int milk) {
        this.milkContainer += milk;
    }

    public String getStatusText() {
        return "Milk: " + getMilk() + " / " + getMilkMax();
    }
}
