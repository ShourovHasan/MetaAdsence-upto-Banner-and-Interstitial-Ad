package com.esbd.metaaddtestapp;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("f390f2ec-aad9-412d-9796-a39a6cfa91ea");
    }
}
