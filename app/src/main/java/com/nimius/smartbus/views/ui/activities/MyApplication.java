package com.nimius.smartbus.views.ui.activities;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultEncodedMemoryCacheParamsSupplier;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import io.fabric.sdk.android.Fabric;
import io.intercom.android.sdk.Intercom;

public class MyApplication extends Application {

    private String TAG = "--MyApplication--";
    private static MyApplication context;
    static final String YOUR_API_KEY = "android_sdk-ba50eb806b474663eeca2ccc0e185dc02616eacc";
    static final String YOUR_APP_ID = "cobrq4fx";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: called");
        Fabric.with(this, new Crashlytics());

        /*fresco*/
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setEncodedMemoryCacheParamsSupplier(new DefaultEncodedMemoryCacheParamsSupplier())
                .build();
        Fresco.initialize(this, config);


        Intercom.initialize(this, YOUR_API_KEY, YOUR_APP_ID);

    }


}