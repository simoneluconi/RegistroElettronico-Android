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
        } else if (remoteMessage?.data != null && PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notify", true)) {

            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(applicationContext))
            val notificationJob = dispatcher.newJobBuilder()
                    .setService(NotificationService::class.java)
                    .setTag("sharpdroid-notification-service")
                    .setTrigger(Trigger.NOW)
                    .setRecurring(false)
                    .setReplaceCurrent(true)
                    .addConstraint(Constraint.ON_ANY_NETWORK)
                    .addConstraint(Constraint.DEVICE_IDLE)

            dispatcher.mustSchedule(notificationJob.build())

        }
    }
}
