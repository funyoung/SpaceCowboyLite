package com.quchen.spacecowboy.activity;

import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

import com.quchen.spacecowboy.Util;

class AbstractMainActivity extends FragmentActivity {

    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        pause();
        super.onPause();
    }

    protected void setDisplaySpecs() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Util.DENSITY = displayMetrics.density;
        Util.DISPLAX_SIZE = displayMetrics.heightPixels / displayMetrics.densityDpi;
        Util.PIXEL_HEIGHT = displayMetrics.heightPixels;
        Util.PIXEL_WIDTH = displayMetrics.widthPixels;
        Util.ORIENTATION = getWindow().getWindowManager().getDefaultDisplay().getRotation();
    }

    protected void setupEvironment() {
        Config.readVolume(this);
        Util.initMusicPlayer(this);
    }

    private void stop() {
        if (Util.musicPlayer != null) {
            Util.musicPlayer.stop();
        }
    }

    private void start() {
        if (Util.musicPlayer != null) {
            Util.musicPlayer.start();
        }
    }

    private void pause() {
        if (Util.musicPlayer != null) {
            Util.musicPlayer.pause();
        }
    }
}
