package com.quchen.spacecowboy.activity;
/**
 * Main activity of the game.
 * Provides the touchevents and hold the tiltSensorobject
 *
 * @author lars
 */
//import com.google.android.gms.games.GamesClient;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quchen.spacecowboy.AccomplishmentsBox;
import com.quchen.spacecowboy.GameView;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.TiltSensor;
import com.quchen.spacecowboy.TimerExec;
import com.quchen.spacecowboy.TimerExecTask;
import com.quchen.spacecowboy.Util;

public class Game extends AbstractMainActivity implements OnTouchListener {

    /**
     * Makes the game instance accessible for everyone
     */
    public static Game theGame;
    private final AccomplishmentsBox outbox = new AccomplishmentsBox();
    private final long gameTimerTick = 1000;
    public TimerExec gameTimer = new TimerExec(gameTimerTick, -1, new TimerExecTask() {
        @Override
        public void onTick() {
            checkAchievments();
        }

        @Override
        public void onFinish() {
        }
    });
    private GameView view;
    private TiltSensor tiltSensor;
    private TextView stats;
    private AccomplishmentsBox outboxLocal;
    private volatile short milk = Util.START_MILK;
    private volatile short milkContainer = Util.START_CONTAINER;
    private Dialog inGameMenu;
    //achievments
    private boolean catchedDancecowFrozen;
    private boolean healedPoison;
    private boolean destroyed10MeteoroidsWitchShield;
    private boolean redCoinCollected;
    private long lastTimeCoin;
    private long lowMilkTime = -1;
    private long fullMilkTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDisplaySpecs();

        super.onCreate(savedInstanceState);

        setupEvironment();

        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        theGame = this;
        setUpGameSettings();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setLayouts();
        setupInGameMenu();

        view.setOnTouchListener(this);

        tiltSensor = new TiltSensor(view, (SensorManager) getSystemService(Context.SENSOR_SERVICE));

        gameTimer.start();
    }

    private void setUpGameSettings() {
        Util.lvl = 0;
        outboxLocal = AccomplishmentsBox.getLocal(this);

        this.milk = (short) (Util.START_MILK + 2 * Shop.getBoughItems(this, Shop.moreStartMilkSave));
        Util.COIN_COLLISION_FACTOR = Util.DISTANCE_COLLISION_FACTOR
                + Util.DISTANCE_COLLISION_FACTOR * Shop.getBoughItems(this, Shop.coinMagnetSave) / 2;
        Util.COW_COLLISION_FACTOR = Util.DISTANCE_COLLISION_FACTOR
                + Util.DISTANCE_COLLISION_FACTOR * Shop.getBoughItems(this, Shop.cowMagnetSave) / 2;
        Util.ROCK_COLLISION_FACTOR = Util.DISTANCE_COLLISION_FACTOR
                - Util.DISTANCE_COLLISION_FACTOR * Shop.getBoughItems(this, Shop.betterRocketSave) * 5 / 12;

        Util.GUIDED_ROCK_SPEED_FACTOR = 1.0f / (Shop.getBoughItems(this, Shop.guidedRockProtectionSave) + 1);
        Util.STATUS_EFFECT_FACTOR = 1.0f / (Shop.getBoughItems(this, Shop.statusEffectReductionSave) + 1);
        Util.ATTACK_AREA_EFFECT = (int) getResources().getDisplayMetrics().density *
                (Util.ATTACK_AREA_EFFECT_INCREASE + Shop.getBoughItems(this, Shop.explosionAttackSave));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerExec.stopAllTimers();
    }


    private void setupInGameMenu() {
        inGameMenu = new Dialog(this);
        inGameMenu.setContentView(R.layout.ingamemenu);
        inGameMenu.setCancelable(true);

        ((Button) inGameMenu.findViewById(R.id.ingamemenuYes)).setTextSize(Util.getTextSize());
        ((Button) inGameMenu.findViewById(R.id.ingamemenuNo)).setTextSize(Util.getTextSize());
        ((Button) inGameMenu.findViewById(R.id.ingamemenuSettings)).setTextSize(Util.getTextSize());
        ((TextView) inGameMenu.findViewById(R.id.ingamemenuText)).setTextSize(Util.getTextSize());


        inGameMenu.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onResume();
            }
        });
        inGameMenu.findViewById(R.id.ingamemenuYes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inGameMenu.findViewById(R.id.ingamemenuNo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inGameMenu.dismiss();
            }
        });
        inGameMenu.findViewById(R.id.ingamemenuSettings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.quchen.spacecowboy.activity.Config"));
            }
        });
    }

    private void setLayouts() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        view = new GameView(this);
        LinearLayout topLayout = new LinearLayout(this);

        Button pauseButton = new Button(this);
        pauseButton.setText("  ||  ");
        pauseButton.setTextSize(Util.getTextSize());
        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                inGameMenu.show();
            }
        });

        stats = new TextView(this);
        stats.setTextSize(Util.getTextSize());
        topLayout.addView(pauseButton);
        topLayout.addView(stats);

        mainLayout.addView(topLayout);
        mainLayout.addView(view);

        setContentView(mainLayout);
    }

    public void updateStatsText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stats.setText("\t\t" + "Milk: " + getMilk() + " / " + getMilkMax()
                        + "\t\t\t\t\t\t" + "LVL: " + Util.lvl
                        + "\t\t\t\t\t\t" + "Coins: " + getAccomplishments().score);
            }
        });

    }

    @Override
    protected void onPause() {
        tiltSensor.onPause();
        view.pause();
        if (Util.musicPlayer != null) {
            Util.musicPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        inGameMenu.cancel();
        tiltSensor.onResume();
        view.resume();
        if (Util.musicPlayer != null) {
            Util.musicPlayer.start();
        }
        super.onResume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            view.Touched((int) event.getX(), (int) event.getY());
        }
        return true;
    }

    public void increasePoints(int value) {
        lastTimeCoin = this.gameTimer.getElapsedTime();
        outbox.score += value;
        int tmp = Util.lvl;
        Util.lvl = (short) (outbox.score / Util.COINS_FOR_LEVELUP);
        if (tmp < Util.lvl) {
            showToast("Level-Up");
        }
    }

    public AccomplishmentsBox getAccomplishments() {
        return this.outbox;
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
            this.lowMilkTime = -1;
        }
        if (this.milk == this.milkContainer && oldMilk != this.milkContainer) {
            this.fullMilkTime = this.gameTimer.getElapsedTime();
        }
    }

    public void decreaseMilk(int milk) {
        this.milk -= milk;

        if (this.milk == 1 && milk != 0) {
            this.lowMilkTime = this.gameTimer.getElapsedTime();
        }
        if (this.milk != this.milkContainer) {
            this.fullMilkTime = -1;
        }
    }

    public int getMilkMax() {
        return milkContainer;
    }

    public void increaseMilkMax(int milk) {
        this.milkContainer += milk;
    }

    @Override
    public void onBackPressed() {
        this.onPause();
        inGameMenu.show();
    }

    public void showToast(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAchievments() {
//		GamesClient gamesClient = getGameHelper().getGamesClient();
//
//		if(gameTimer.getElapsedTime() > 5*60*1000){
//			if(!outboxLocal.survived_5 && !outbox.survived_5
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_survived_5));
//			}
//			outbox.survived_5 = true;
//		}
//		if(gameTimer.getElapsedTime() > 15*60*1000){
//			if(!outboxLocal.survived_15 && !outbox.survived_15
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_survived_15));
//			}
//			outbox.survived_15 = true;
//		}
//		if(gameTimer.getElapsedTime() - this.view.getRocket().getLastTimeDamaged() >= 60*1000){
//			if(!outboxLocal.undamaged_1 && !outbox.undamaged_1
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_undamaged_1));
//			}
//			outbox.undamaged_1 = true;
//		}
//		if(gameTimer.getElapsedTime() - this.view.getRocket().getLastTimeDamaged() >= 5*60*1000){
//			if(!outboxLocal.undamaged_5 && !outbox.undamaged_5
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_undamaged_5));
//			}
//			outbox.undamaged_5 = true;
//		}
//		if(gameTimer.getElapsedTime() - this.lastTimeCoin >= 30*1000){
//			sleepyCowboy();
//
//			if(!outboxLocal.sleepy_cowboy && !outbox.sleepy_cowboy
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_sleepy_cowboy));
//			}
//			outbox.sleepy_cowboy = true;
//		}
//		if(outbox.meteoroids >= 50){
//			if(!outboxLocal.meteoroids_50_run && !outbox.meteoroids_50_run
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_meteoroids_50_run));
//			}
//			outbox.meteoroids_50_run = true;
//		}
//		if(outbox.meteoroids >= 200){
//			if(!outboxLocal.meteoroids_200_run && !outbox.meteoroids_200_run
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_meteoroids_200_run));
//			}
//			outbox.meteoroids_200_run = true;
//		}
//		if(outbox.cows >= 75){
//			if(!outboxLocal.cows_75_run && !outbox.cows_75_run
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_cows_75_run));
//			}
//			outbox.cows_75_run = true;
//		}
//		if(outbox.cows >= 200){
//			if(!outboxLocal.cows_200_run && !outbox.cows_200_run
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_cows_200_run));
//			}
//			outbox.cows_200_run = true;
//		}
//		if(outbox.powerups >= 30){
//			if(!outboxLocal.powerUps_30_run && !outbox.powerUps_30_run
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_powerups_30_run));
//			}
//			outbox.powerUps_30_run = true;
//		}
//		if(milkContainer >= 100){
//			if(!outboxLocal.milkContainer_100 && !outbox.milkContainer_100
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_milkcontainer_100));
//			}
//			outbox.milkContainer_100 = true;
//		}
//		if(catchedDancecowFrozen){
//			if(!outboxLocal.catch_dancecow_frozen && !outbox.catch_dancecow_frozen
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_catch_dancecow_frozen));
//			}
//			outbox.catch_dancecow_frozen = true;
//			this.catchedDancecowFrozen = false;
//		}
//		if(healedPoison){
//			if(!outboxLocal.heal_poison && !outbox.heal_poison
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_heal_poison));
//			}
//			outbox.heal_poison = true;
//			this.healedPoison = false;
//		}
//		if(destroyed10MeteoroidsWitchShield){
//			if(!outboxLocal.meteoroids_shield_10 && !outbox.meteoroids_shield_10
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_meteoroids_with_shield_10));
//			}
//			outbox.meteoroids_shield_10 = true;
//			this.destroyed10MeteoroidsWitchShield = false;
//		}
//		if(Util.lvl >= 10 && milkContainer == Util.START_CONTAINER){
//			if(!outboxLocal.lvl_10_10_milkcontainer && !outbox.lvl_10_10_milkcontainer
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_lvl_10_with_10_milkcontainer));
//			}
//			outbox.lvl_10_10_milkcontainer = true;
//		}
//		if(redCoinCollected){
//			if(!outboxLocal.red_coin && !outbox.red_coin
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_red_coin));
//			}
//			outbox.red_coin = true;
//			this.redCoinCollected = false;
//		}
//		if(this.lowMilkTime != -1 && gameTimer.getElapsedTime() - this.lowMilkTime >= 60*1000){
//			if(!outboxLocal.low_milk_1_minutes && !outbox.low_milk_1_minutes
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_low_milk_1_minutes));
//			}
//			outbox.low_milk_1_minutes = true;
//		}
//		if(this.fullMilkTime != -1 && gameTimer.getElapsedTime() - this.fullMilkTime >= 2*60*1000){
//			if(!outboxLocal.full_milk_2_minutes && !outbox.full_milk_2_minutes
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_full_milk_2_minutes));
//			}
//			outbox.full_milk_2_minutes = true;
//		}
//		if(outboxLocal.cows + outbox.cows >= 100){
//			if(!outboxLocal.cows_100 && !outbox.cows_100
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_cows_100));
//			}
//			outbox.cows_100 = true;
//		}
//		if(outboxLocal.cows + outbox.cows >= 1000){
//			if(!outboxLocal.cows_1000 && !outbox.cows_1000
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_cows_1000));
//			}
//			outbox.cows_1000 = true;
//		}
//		if(outboxLocal.meteoroids + outbox.meteoroids >= 100){
//			if(!outboxLocal.meteoroids_100 && !outbox.meteoroids_100
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_meteoroids_100));
//			}
//			outbox.meteoroids_100 = true;
//		}
//		if(outboxLocal.meteoroids + outbox.meteoroids >= 1000){
//			if(!outboxLocal.meteoroids_1000 && !outbox.meteoroids_1000
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_meteoroids_1000));
//			}
//			outbox.meteoroids_1000 = true;
//		}
//		if(outboxLocal.powerups + outbox.powerups >= 100){
//			if(!outboxLocal.powerUps_100 && !outbox.powerUps_100
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_powerups_100));
//			}
//			outbox.powerUps_100 = true;
//		}
//		if((outbox.catch_them_all | outboxLocal.catch_them_all) == AccomplishmentsBox.catched_them_all){
//			if(!outboxLocal.catched_them_all_bool && !outbox.catched_them_all_bool
//					 && getGameHelper().isSignedIn()){
//				gamesClient.unlockAchievement(getResources().getString(R.string.achievement_catch_them_all));
//			}
//			outbox.catched_them_all_bool = true;
//		}
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
        showToast("Are your Sleeping?\n" +
                "Collect Coins!");
        view.punishment = true;
        lastTimeCoin = this.gameTimer.getElapsedTime();
    }

//	@Override
//	public void onSignInFailed() {}
//
//	@Override
//	public void onSignInSucceeded() {}

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

}
