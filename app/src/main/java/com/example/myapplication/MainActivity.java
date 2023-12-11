package com.example.myapplication;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private RewardedAd rewardedAd;
    private long lastAdTime = 0;
    private static final int AD_INTERVAL = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("https://ia800300.us.archive.org/17/items/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4"));

        videoView.setMediaController(new MediaController(MainActivity.this));

        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        RewardedAd.load(MainActivity.this, "ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                MainActivity.this.rewardedAd = rewardedAd;
            }
        });

        new CountDownTimer(AD_INTERVAL, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (System.currentTimeMillis() - lastAdTime >= AD_INTERVAL) {
                    if (rewardedAd != null) {
                        videoView.pause();
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                rewardedAd = null;
                                videoView.start();
                                RewardedAd.load(MainActivity.this, "ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                        super.onAdLoaded(rewardedAd);
                                        MainActivity.this.rewardedAd = rewardedAd;
                                    }
                                });
                            }
                        });
                        rewardedAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                            }
                        });
                        lastAdTime = System.currentTimeMillis();
                    }
                }
                start();
            }
        }.start();
    }
}
