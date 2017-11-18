package com.sharpdroid.registroelettronico.notification

import android.preference.PreferenceManager
import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Trigger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage?.notification != null) {
            super.onMessageReceived(remoteMessage)
        } else if (remoteMessage?.data != null &&
                PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notify", true)) {

            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(applicationContext))
            val notificationJob = dispatcher.newJobBuilder()
                    // the JobService that will be called
                    .setService(NotificationService::class.java)
                    // uniquely identifies the job
                    .setTag("sharpdroid-notification-service")
                    // start now
                    .setTrigger(Trigger.NOW)
                    // one-off job
                    .setRecurring(false)
                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(true)
                    // constraints that need to be satisfied for the job to run
                    .setConstraints(
                            Constraint.ON_ANY_NETWORK,
                            Constraint.DEVICE_IDLE
                    )

            dispatcher.mustSchedule(notificationJob.build())
        }
    }
}