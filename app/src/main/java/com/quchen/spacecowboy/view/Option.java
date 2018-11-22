package com.quchen.spacecowboy.view;

import android.app.Activity;
import android.content.SharedPreferences;

import com.quchen.spacecowboy.utility.Util;

public class Option {
    /**
     * Name of SharedPreferenc
     */
    private static final String prefsName = "CONFIG";
    private static final String musicVolume = "musicVolume";
    private static final String soundVolume = "soundVolume";


    public static final String betterRocketSave = "shopRocket";
    public static final String moreStartMilkSave = "shopStartMilk";
    public static final String coinMagnetSave = "shopCoinMagnet";
    public static final String cowMagnetSave = "shopCowMagnet";
    public static final String guidedRockProtectionSave = "guidedRockProtection";
    public static final String statusEffectReductionSave = "statusEffectReduction";
    public static final String explosionAttackSave = "explosionAttack";
    private static final String saveName = "shop";

    public static void saveVolume(Activity activity) {
        SharedPreferences saves = activity.getSharedPreferences(prefsName, 0);
        SharedPreferences.Editor editor = saves.edit();
        editor.putFloat(musicVolume, Util.musicVolume);
        editor.putFloat(soundVolume, Util.soundVolume);
        editor.commit();
    }

    public static void readVolume(Activity activity) {
        Util.musicVolume = activity.getSharedPreferences(prefsName, 0).getFloat(musicVolume, 0.5f);
        Util.soundVolume = activity.getSharedPreferences(prefsName, 0).getFloat(soundVolume, 0.5f);
    }

    public static int getBoughItems(Activity activity, String item) {
        SharedPreferences saves = activity.getSharedPreferences(saveName, 0);
        return saves.getInt(item, 0);
    }
}
