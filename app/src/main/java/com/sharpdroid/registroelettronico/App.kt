package com.sharpdroid.registroelettronico

import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.google.firebase.FirebaseApp
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import io.fabric.sdk.android.Fabric


class App : MultiDexApplication() {

    private val TAG = "App"
    private val DEVELOPER_MODE = false

    override fun onCreate() {
        super.onCreate()
        DatabaseHelper.createDb(this)
        FirebaseApp.initializeApp(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.deleteNotificationChannel("sharpdroid_registro_channel_01")
            notificationManager.deleteNotificationChannel("sharpdroid_registro_channel_02")
        }

        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDialog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}