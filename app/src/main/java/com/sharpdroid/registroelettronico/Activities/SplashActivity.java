package com.sharpdroid.registroelettronico.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.sharpdroid.registroelettronico.BuildConfig;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Install a hook to Crashlytics and Answers (only in production releases)
        if (!BuildConfig.DEBUG) {
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Kit[]{new Crashlytics(), new Answers()})
                    .build();
            Fabric.with(fabric);
        }
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }
}