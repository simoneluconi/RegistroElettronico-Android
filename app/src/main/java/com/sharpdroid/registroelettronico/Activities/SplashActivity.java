package com.sharpdroid.registroelettronico.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.sharpdroid.registroelettronico.BuildConfig;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics(), new Answers());
        }
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }
}