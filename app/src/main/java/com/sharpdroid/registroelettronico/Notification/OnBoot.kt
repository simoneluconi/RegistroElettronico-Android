package com.sharpdroid.registroelettronico.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import java.util.concurrent.TimeUnit

class OnBoot : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val periodicity = TimeUnit.HOURS.toSeconds(1).toInt()
        val toleranceInterval = TimeUnit.MINUTES.toSeconds(10).toInt()

        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
        val notificationJob = dispatcher.newJobBuilder()
                .setService(NotificationService::class.java)
                .setTag("sharpdroid-notification-service")
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)

        dispatcher.mustSchedule(notificationJob.build())
    }
}
