package com.sharpdroid.registroelettronico

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.google.firebase.FirebaseApp
import com.orm.SugarContext
import io.fabric.sdk.android.Fabric

class App : Application() {

    private val TAG = "App"

    override fun onCreate() {
        super.onCreate()
        SugarContext.init(this)
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