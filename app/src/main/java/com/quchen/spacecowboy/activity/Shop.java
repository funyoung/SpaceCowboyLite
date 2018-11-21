package com.quchen.spacecowboy.activity;
/**
 * The shopactivity for buying upgrades
 *
 * @author lars
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.quchen.spacecowboy.utility.AccomplishmentsBox;
import com.quchen.spacecowboy.R;
import com.quchen.spacecowboy.sprite.Sprite;
import com.quchen.spacecowboy.utility.Util;

import java.util.ArrayList;
import java.util.List;

public class Shop extends Activity {

    public static final String betterRocketSave = "shopRocket";
    public static final String moreStartMilkSave = "shopStartMilk";
    public static final String coinMagnetSave = "shopCoinMagnet";
    public static final String cowMagnetSave = "shopCowMagnet";
    public static final String guidedRockProtectionSave = "guidedRockProtection";
    public static final String statusEffectReductionSave = "statusEffectReduction";
    public static final String explosionAttackSave = "explosionAttack";
    private static final String saveName = "shop";
    private final int betterRocketMax = 1;
    private final int betterRocketPrice = 2000;
    private final int moreStartMilkMax = 3;
    private final int moreStartMilkPrice = 100;
    private final int coinMagnetMax = 2;
    private final int coinMagnetPrice = 800;
    private final int cowMagnetMax = 2;
    private final int cowMagnetPrice = 800;
    private final int guidedRockProtectionMax = 1;
    private final int guidedRockProtectionPrice = 800;
    private final int statusEffectReductionMax = 1;
    private final int statusEffectReductionPrice = 600;
    private final int explosionAttackMax = 2;
    private final int explosionAttackPrice = 1500;


    private List<Buyable> buyables = new ArrayList<Buyable>();

    public static int getBoughItems(Activity activity, String item) {
        SharedPreferences saves = activity.getSharedPreferences(saveName, 0);
        return saves.getInt(item, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);

        setUpTopMenu();
        setUpBuyables();
        setUpListView();
        registerClickCallback();
    }

    private void setUpTopMenu() {
        ImageView coinIcon = (ImageView) findViewById(R.id.shop_coin_icon);
        coinIcon.setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.coin_single)));
        TextView coinAmount = (TextView) findViewById(R.id.shop_coin_amount);
        coinAmount.setText("" + AccomplishmentsBox.getCoins(this));
        coinAmount.setTextSize(Util.getTextSize());

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setTextSize(Util.getTextSize());
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUpBuyables() {
        buyables.clear();

        buyables.add(new Buyable(R.drawable.shop_better_rocket, R.string.shopBetterRocket,
                getBoughItems(betterRocketSave), betterRocketMax,
                betterRocketPrice, betterRocketSave));
        buyables.add(new Buyable(R.drawable.shop_more_start_milk, R.string.shopMoreStartMilk,
                getBoughItems(moreStartMilkSave), moreStartMilkMax,
                moreStartMilkPrice, moreStartMilkSave));
        buyables.add(new Buyable(R.drawable.shop_coin_magnet, R.string.shopCoinMagnet,
                getBoughItems(coinMagnetSave), coinMagnetMax,
                coinMagnetPrice, coinMagnetSave));
        buyables.add(new Buyable(R.drawable.shop_cow_magnet, R.string.shopCowMagnet,
                getBoughItems(cowMagnetSave), cowMagnetMax,
                cowMagnetPrice, cowMagnetSave));
        buyables.add(new Buyable(R.drawable.shop_guided_rock_protection, R.string.shopGuidedRockProtection,
                getBoughItems(guidedRockProtectionSave), guidedRockProtectionMax,
                guidedRockProtectionPrice, guidedRockProtectionSave));
        buyables.add(new Buyable(R.drawable.shop_status_effect_reduction, R.string.shopStatusEffectReduction,
                getBoughItems(statusEffectReductionSave), statusEffectReductionMax,
                statusEffectReductionPrice, statusEffectReductionSave));
        buyables.add(new Buyable(R.drawable.shop_explosion_attack, R.string.shopExplosionAttack,
                getBoughItems(explosionAttackSave), explosionAttackMax,
                explosionAttackPrice, explosionAttackSave));
    }

    private void setBoughtItems(String item, int value) {
        SharedPreferences saves = getSharedPreferences(saveName, 0);
        SharedPreferences.Editor editor = saves.edit();
        editor.putInt(item, value);
        editor.commit();
    }

    public int getBoughItems(String item) {
        SharedPreferences saves = getSharedPreferences(saveName, 0);
        return saves.getInt(item, 0);
    }

    private void setUpListView() {
        ArrayAdapter<Buyable> adapter = new BuyableListAdapter();
        ListView list = (ListView) findViewById(R.id.buyableListView);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.buyableListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                if (buy(position)) {
                    // Reload UI
                    setUpTopMenu();
                    setUpBuyables();
                    setUpListView();
                    registerClickCallback();
                }
            }
        });
    }

    private boolean buy(int pos) {
        Buyable currentBuyable = buyables.get(pos);
        if (AccomplishmentsBox.getCoins(Shop.this) >= currentBuyable.price
                && currentBuyable.currentLvl < currentBuyable.maxLvl) {
            AccomplishmentsBox.decreaseCoins(Shop.this, currentBuyable.price);
            setBoughtItems(currentBuyable.save, currentBuyable.currentLvl + 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.musicPlayer != null) {
            Util.musicPlayer.start();
        }

    }

    @Override
    protected void onPause() {
        if (Util.musicPlayer != null) {
            Util.musicPlayer.pause();
        }
        super.onPause();
    }

    private class Buyable {
        private int iconID;
        private int stringID;
        private int currentLvl;
        private int maxLvl;
        private int price;
        private String save;
        public Buyable(int iconID, int stringID, int currentLvl, int maxLvl, int price, String save) {
            super();
            this.iconID = iconID;
            this.stringID = stringID;
            this.currentLvl = currentLvl;
            this.maxLvl = maxLvl;
            this.price = price * (currentLvl + 1);
            this.save = save;
        }
    }

    private class BuyableListAdapter extends ArrayAdapter<Buyable> {
        public BuyableListAdapter() {
            super(Shop.this, R.layout.buyable_item_view, buyables);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.buyable_item_view, parent, false);
            }

            Buyable currentBuyable = buyables.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.buyable_icon);
            imageView.setImageBitmap(Sprite.createBitmap(getResources().getDrawable(currentBuyable.iconID)));

            TextView progress = (TextView) itemView.findViewById(R.id.buyable_progress);
            progress.setText("" + currentBuyable.currentLvl + " / " + currentBuyable.maxLvl);
            progress.setTextSize(Util.getTextSize());

            TextView price = (TextView) itemView.findViewById(R.id.buyable_price);
            if (currentBuyable.currentLvl < currentBuyable.maxLvl) {
                price.setText("" + currentBuyable.price + " coins");
            } else {
                price.setText(getResources().getString(R.string.shopFullUpgraded));
            }
            price.setTextSize(Util.getTextSize());

            TextView info = (TextView) itemView.findViewById(R.id.buyable_info);
            info.setText(getResources().getString(currentBuyable.stringID));
            info.setTextSize(Util.getTextSize());

            return itemView;
        }
    }

}
