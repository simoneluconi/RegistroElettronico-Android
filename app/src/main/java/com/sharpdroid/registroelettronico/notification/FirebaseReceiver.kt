package com.sharpdroid.registroelettronico.notification

import android.content.Intent
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
                    // start between 0 and 60 seconds from now
                    .setTrigger(Trigger.executionWindow(0, 60))
                    // one-off job
                    .setRecurring(false)
                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(false)
                    // constraints that need to be satisfied for the job to run
                    .setConstraints(
                            // only run when a network connection is available
                            Constraint.ON_ANY_NETWORK,
                            // only run when the device is idle
                            Constraint.DEVICE_IDLE
                    )

            dispatcher.mustSchedule(notificationJob.build())
        }
    }
}