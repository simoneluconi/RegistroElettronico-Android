package com.sharpdroid.registroelettronico.Notification

import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Databases.Entities.Option
import com.sharpdroid.registroelettronico.Utils.Account

class NotificationService : JobService() {
    override fun onStopJob(job: JobParameters?): Boolean {
        return false
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        val option: Option = SugarRecord.findById(Option::class.java, Account.with(applicationContext).user) ?: return false

        Log.d("NotificationService", "FIRE NOTIFICATION")

        return false //do not retry
    }
}