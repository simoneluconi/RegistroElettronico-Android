package com.sharpdroid.registroelettronico.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.sharpdroid.registroelettronico.BuildConfig

import io.fabric.sdk.android.Fabric

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics(), Answers())
        }
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }
}