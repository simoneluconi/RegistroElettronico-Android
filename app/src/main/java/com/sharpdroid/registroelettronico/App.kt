package com.sharpdroid.registroelettronico

import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.google.firebase.FirebaseApp
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import io.fabric.sdk.android.Fabric

class App : MultiDexApplication() {

    private val TAG = "App"

    override fun onCreate() {
        super.onCreate()
        DatabaseHelper.createDb(this)
        FirebaseApp.initializeApp(this)

        // Install a hook to Crashlytics and Answers (only in production releases)
        if (!BuildConfig.DEBUG) {
            try {
                Fabric.with(this, Crashlytics(), Answers())
            } catch (e: Error) {
                // Ignore any fabric exception by miss-configuration
                Log.e(TAG, "Cannot configure Fabric", e)
            }

        }
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}