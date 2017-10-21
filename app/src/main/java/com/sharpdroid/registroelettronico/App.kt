package com.sharpdroid.registroelettronico

import android.app.Application
import android.content.Intent
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import com.google.android.gms.security.ProviderInstaller
import com.orm.SugarContext
import com.sharpdroid.registroelettronico.Notification.NotificationService
import java.util.concurrent.TimeUnit

class App : Application() {

    private val TAG = "App"
    lateinit var dispatcher: FirebaseJobDispatcher

    override fun onCreate() {
        super.onCreate()
        upgradeSecurityProvider()
        SugarContext.init(this)

        val periodicity = TimeUnit.HOURS.toSeconds(1).toInt()
        val toleranceInterval = TimeUnit.MINUTES.toSeconds(10).toInt()

        dispatcher = FirebaseJobDispatcher(GooglePlayDriver(applicationContext))
        val notificationJob = dispatcher.newJobBuilder()
                .setService(NotificationService::class.java)
                .setTag("sharpdroid-notification-service")
                .setTrigger(Trigger.executionWindow(periodicity, periodicity - toleranceInterval))
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)

        dispatcher.mustSchedule(notificationJob.build())
    }

    private fun upgradeSecurityProvider() {
        ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {

            }

            override fun onProviderInstallFailed(i: Int, intent: Intent) {
                Log.w(TAG, "Failed installing security provider, error code: " + i)
            }
        })
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}