package com.quchen.spacecowboy;
/**
 * Displays the splashscreen at the start of the app.
 * Provides buttons to settings, highscore, gamestart, ...
 * @author lars
 */
import com.google.example.games.basegameutils.GameActivity;
import com.quchen.spacecowboy.R.drawable;
import com.quchen.spacecowboy.R.id;
import com.quchen.spacecowboy.R.layout;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends GameActivity {
	
	private ImageButton exitButton;
	private ImageButton helpButton;
	private ImageButton highscoreButton;
	private ImageButton highscoreButtonOffline;
	private ImageButton achievementButton;
	private ImageButton settingsButton;
	private ImageButton playButton;
	private ImageButton shopButton;
	private Button about;
//	private SignInButton signInButton;
	private Button signOutButton;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setDisplaySpecs();
		this.setContentView(layout.activity_main);
		this.setUp();
		Config.readVolume(this);
		Util.initMusicPlayer(this);
		Util.musicPlayer.start();
	}
	
	private void setDisplaySpecs(){
		Util.DENSITY = this.getResources().getDisplayMetrics().density;
		Util.DISPLAX_SIZE = this.getResources().getDisplayMetrics().heightPixels / this.getResources().getDisplayMetrics().densityDpi;
		Util.PIXEL_HEIGHT = this.getResources().getDisplayMetrics().heightPixels;
		Util.PIXEL_WIDTH = this.getResources().getDisplayMetrics().widthPixels;
		Util.ORIENTATION = this.getWindow().getWindowManager().getDefaultDisplay().getRotation();
	}
	
	private void setUp(){
		this.exitButton = this.findViewById(id.exitButton);
		this.exitButton.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.exit_button)));
		this.exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.finish();
			}
		});

		this.helpButton = this.findViewById(id.helpButton);
		this.helpButton.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.help_button)));
		this.helpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent("com.quchen.spacecowboy.Help"));
			}
		});

		this.highscoreButton = this.findViewById(id.highscoreButton);
		this.highscoreButton.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.highscore)));
		this.highscoreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivityForResult(MainActivity.this.mHelper.getGamesClient().getLeaderboardIntent(
//						getResources().getString(R.string.leaderboard_space_cowboy)), 0);
//				startActivity(MainActivity.this.mHelper.getGamesClient().getAllLeaderboardsIntent());
			}
		});

		this.highscoreButtonOffline = this.findViewById(id.highscoreButtonOffline);
		this.highscoreButtonOffline.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.highscore)));
		this.highscoreButtonOffline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent("com.quchen.spacecowboy.HighscoreOffline"));
			}
		});

		this.shopButton = this.findViewById(id.shopButton);
		this.shopButton.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.shop)));
		this.shopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent("com.quchen.spacecowboy.Shop"));
			}
		});

		this.achievementButton = this.findViewById(id.achievementButton);
		this.achievementButton.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.achievement_button)));
		this.achievementButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivityForResult(MainActivity.this.mHelper.getGamesClient().getAchievementsIntent(),0);
			}
		});

		this.settingsButton = this.findViewById(id.settingsButton);
		this.settingsButton.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.config_button)));
		this.settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent("com.quchen.spacecowboy.Config"));
			}
		});

		this.playButton = this.findViewById(id.playButton);
		this.playButton.setImageBitmap(Sprite.createBitmap(this.getResources().getDrawable(drawable.play_button)));
		this.playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent("com.quchen.spacecowboy.Game"));
			}
		});

		this.about = this.findViewById(id.about);
		this.about.setTextSize(Util.getTextSize());
		this.about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent("com.quchen.spacecowboy.About"));
			}
		});

//		signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//		signInButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				beginUserInitiatedSignIn();
//			}
//		});

		this.signOutButton = this.findViewById(id.sign_out_button);
		this.signOutButton.setTextSize(Util.getTextSize());
		this.signOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				signOut();
				MainActivity.this.showOfflineButtons();
			}
		});
	}

	@Override
	protected void onDestroy() {
		if(Util.musicPlayer != null){
			Util.musicPlayer.stop();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Util.musicPlayer != null){
			Util.musicPlayer.start();
		}

	}

	@Override
	protected void onPause() {
		if(Util.musicPlayer != null){
			Util.musicPlayer.pause();
		}
		super.onPause();
	}

//	@Override
//	public void onSignInFailed() {
//		showOfflineButtons();
//		Toast.makeText(getApplicationContext(), "Not signed in", Toast.LENGTH_SHORT).show();
//	}
//
//	@Override
//	public void onSignInSucceeded() {
//		Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
//		showOnlineButtons();
//		if(!AccomplishmentsBox.isOnline(this)){
//			AddScore.submitScore(this, AccomplishmentsBox.getLocal(this), getGamesClient());
//		}
//	}

//	private void showOnlineButtons(){
//		this.findViewById(id.achievementButton).setVisibility(View.VISIBLE);
//		this.findViewById(id.highscoreButton).setVisibility(View.VISIBLE);
//		this.findViewById(id.sign_in_button).setVisibility(View.GONE);
//		this.findViewById(id.sign_out_button).setVisibility(View.VISIBLE);
//		this.findViewById(id.sign_in_button_help_text).setVisibility(View.GONE);
//		this.findViewById(id.highscoreButtonOffline).setVisibility(View.GONE);
//	}

	private void showOfflineButtons(){
//		this.findViewById(id.sign_in_button).setVisibility(View.VISIBLE);
		this.findViewById(id.sign_out_button).setVisibility(View.GONE);
		this.findViewById(id.achievementButton).setVisibility(View.GONE);
		this.findViewById(id.highscoreButton).setVisibility(View.GONE);
		this.findViewById(id.sign_in_button_help_text).setVisibility(View.VISIBLE);
		this.findViewById(id.highscoreButtonOffline).setVisibility(View.VISIBLE);
	}
}
