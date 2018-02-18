package com.sharpdroid.registroelettronico

import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
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
    private val DEVELOPER_MODE = false

    override fun onCreate() {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.deleteNotificationChannel("sharpdroid_registro_channel_01")
            notificationManager.deleteNotificationChannel("sharpdroid_registro_channel_02")
        }
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}