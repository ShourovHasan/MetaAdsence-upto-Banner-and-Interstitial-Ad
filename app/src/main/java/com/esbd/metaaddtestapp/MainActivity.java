package com.esbd.metaaddtestapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class MainActivity extends AppCompatActivity {

    LinearLayout adContainer;
    TextView tvDisplay;
    Button showAdButton;

    private AdView adView;
    int bannerAdClicked = 0;
    int interstitialAdClicked = 0;

//==========for interstitialAd===========
    public static final String TAG = "FullScreenAdd";
    private InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        adContainer = findViewById(R.id.banner_container);
        tvDisplay = findViewById(R.id.tvDisplay);
        showAdButton = findViewById(R.id.showAdButton);
//        -----------------------
        loadBannerAd();
        loadInterstitialAd();
        showAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd!=null && interstitialAd.isAdLoaded()){
                    interstitialAd.show();
                }
            }
        });

    }
//        -----------------------
    private void loadBannerAd(){
        long lastClickTime = getLastClickTime("banner");
        if(System.currentTimeMillis() - lastClickTime < 60000) { // 3600000 milliseconds in an hour
            adContainer.setVisibility(View.GONE);
            return; // Skip loading the ad if within one hour of last click
        }
        adView = new AdView(MainActivity.this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);

// Add the ad view to your activity layout
        adContainer.addView(adView);
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Toast.makeText(
                                MainActivity.this,
                                "Error: " + adError.getErrorMessage(),
                                Toast.LENGTH_LONG)
                        .show();
                tvDisplay.append("\n"+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                tvDisplay.append("\n"+"Ad Loaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                bannerAdClicked++;
                tvDisplay.append("\n"+"Ad onAdClicked: "+bannerAdClicked);

                if(bannerAdClicked>=2){
                    if (adView != null) {
                        adView.destroy();
                    }
//                    MainActivity.super.onDestroy();
                    adContainer.setVisibility(View.GONE);
                    saveLastClickTime("banner");
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                tvDisplay.append("\n"+"Ad onLoggingImpression");
            }
        };


// Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());

    }
//        -----------------------



    private void loadInterstitialAd(){
        long lastClickTime = getLastClickTime("interstitial");
        if(System.currentTimeMillis() - lastClickTime < 60000) {
            return; // Skip loading the ad if within one hour of last click
        }

        interstitialAd = new InterstitialAd(this, "YOUR_PLACEMENT_ID");

        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
                tvDisplay.append("\n\nInterstitial ad displayed.");

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                tvDisplay.append("\n\nInterstitial ad dismissed.");

                loadInterstitialAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                tvDisplay.append("\n\n"+"Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                tvDisplay.append("\n\nInterstitial ad is loaded and ready to be displayed!");
                // Show the ad
//                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                interstitialAdClicked++;
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
                tvDisplay.append("\n\nInterstitial ad clicked"+interstitialAdClicked);


                if(interstitialAdClicked>=2){
                    if (interstitialAd != null) {
                        interstitialAd.destroy();
                    }
                    saveLastClickTime("interstitial");
                }

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
                tvDisplay.append("\n\nInterstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

//    }


    private void saveLastClickTime(String adType) {
        SharedPreferences prefs = getSharedPreferences("AdPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(adType + "_lastClickTime", System.currentTimeMillis());
        editor.apply();
    }

    private long getLastClickTime(String adType) {
        SharedPreferences prefs = getSharedPreferences("AdPrefs", MODE_PRIVATE);
        return prefs.getLong(adType + "_lastClickTime", 0);
    }



    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
//        -----------------------
}