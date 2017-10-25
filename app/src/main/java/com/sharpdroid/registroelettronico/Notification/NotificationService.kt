package com.sharpdroid.registroelettronico.Notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.API.V2.APIClient
import com.sharpdroid.registroelettronico.Activities.MainActivity
import com.sharpdroid.registroelettronico.Databases.Entities.*
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Metodi.getStartEnd

class NotificationService : JobService() {
    override fun onStopJob(job: JobParameters?): Boolean {
        return false //need retry?
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        val profile = Profile.getProfile(applicationContext) ?: return false
        val option: Option = SugarRecord.findById(Option::class.java, profile.id) ?: return false
        with(option) {
            if (!notify || !(notifyAgenda || notifyVoti || notifyComunicazioni || notifyNote)) return false
        }

        val notificationsList = mutableMapOf<String, Int>()

        if (option.notifyAgenda) {
            val diff = getAgendaDiff(profile)
            if (diff != 0)
                notificationsList.put("agenda", diff)
        }
        if (option.notifyVoti) {
            val diff = getVotiDiff(profile)
            if (diff != 0)
                notificationsList.put("voti", diff)
        }
        if (option.notifyComunicazioni) {
            val diff = getComunicazioniDiff(profile)
            if (diff != 0)
                notificationsList.put("comunicazioni", diff)
        }
        if (option.notifyNote) {
            val diff = getNoteDiff(profile)
            if (diff != 0)
                notificationsList.put("note", diff)
        }

        notify(notificationsList, PreferenceManager.getDefaultSharedPreferences(this))

        return false //something else to do?
    }

    private fun notify(notificationsList: MutableMap<String, Int>, preferences: SharedPreferences) {
        if (notificationsList.keys.isEmpty()) return

        val sound = preferences.getBoolean("notify_sound", true)
        val vibrate = preferences.getBoolean("notify_vibrate", true)

        if (notificationsList.keys.size == 1) {
            when (notificationsList.keys.toTypedArray()[0]) {
                "agenda" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_agenda, notificationsList["agenda"]!!, notificationsList["agenda"]!!), null, sound, vibrate)
                }
                "voti" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_voti, notificationsList["voti"]!!, notificationsList["voti"]!!), null, sound, vibrate)
                }
                "comunicazioni" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_communication, notificationsList["comunicazioni"]!!, notificationsList["comunicazioni"]!!), null, sound, vibrate)
                }
                "note" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_note, notificationsList["note"]!!, notificationsList["note"]!!), null, sound, vibrate)
                }
            }
        } else {
            pushNotification("Ci sono novità!", null, sound, vibrate)
            return
        }
    }

    private fun getAgendaDiff(profile: Profile): Int {
        val dates = getStartEnd("yyyyMMdd")
        val agenda = APIClient.with(applicationContext, profile).getAgenda(dates[0], dates[1]).blockingFirst()?.getAgenda(profile) ?: return 0
        if (agenda.isEmpty()) return 0

        return agenda.size - SugarRecord.count<RemoteAgenda>(RemoteAgenda::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
    }

    private fun getVotiDiff(profile: Profile): Int {
        var marks = APIClient.with(applicationContext, profile).getGrades().blockingFirst()?.grades ?: return 0
        if (marks.isEmpty()) return 0

        return marks.size - SugarRecord.count<Grade>(Grade::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
    }

    private fun getComunicazioniDiff(profile: Profile): Int {
        val comm = APIClient.with(applicationContext, profile).getBacheca().blockingFirst()?.communications ?: return 0
        return comm.size - SugarRecord.count<Communication>(Communication::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
    }

    private fun getNoteDiff(profile: Profile): Int {
        val note = APIClient.with(applicationContext, profile).getNotes().blockingFirst()?.getNotes(profile) ?: return 0
        return note.size - SugarRecord.count<Note>(Note::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
    }

    private fun pushNotification(title: String, content: String?, sound: Boolean, vibrate: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            val mBuilder: Notification.Builder

            val i = Intent(this, MainActivity::class.java)
            val intent = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, i, 0)

            mBuilder = Notification.Builder(this, if (sound) channelId else channelId_mute)
                    .setContentTitle(title)
                    .setContentIntent(intent)
                    .setAutoCancel(true)

            if (!content.isNullOrEmpty()) mBuilder.setContentText(content)


            val channel = NotificationChannel(channelId, "Registro Elettronico", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.enableVibration(vibrate)
            channel.lightColor = Color.BLUE

            val channelMute = NotificationChannel(channelId_mute, "Registro Elettronico silent", NotificationManager.IMPORTANCE_LOW)
            channelMute.enableLights(true)
            channelMute.enableVibration(vibrate)
            channelMute.lightColor = Color.BLUE

            if (vibrate) {
                channel.vibrationPattern = longArrayOf(250, 250, 250, 250)
            }
            if (sound) {
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_NOTIFICATION).build())
            }

            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(channelMute)
            notificationManager.notify(nNotif, mBuilder.build())
        } else {
            val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
            val mBuilder: NotificationCompat.Builder
            val i = Intent(this, MainActivity::class.java)
            val intent = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, i, 0)

            mBuilder = NotificationCompat.Builder(this, "Registro Elettronico")
                    .setContentTitle(title)
                    .setContentIntent(intent)
                    .setLights(Color.BLUE, 3000, 3000)
                    .setAutoCancel(true)

            if (!content.isNullOrEmpty()) mBuilder.setContentText(content)

            if (vibrate)
                mBuilder.setVibrate(longArrayOf(250, 250, 250, 250))
            if (sound)
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            notificationManager.notify(nNotif, mBuilder.build())
        }
    }

    companion object {
        private const val nNotif: Int = 999
        private val channelId = "sharpdroid_registro_channel_01"
        private val channelId_mute = "sharpdroid_registro_channel_02"
    }
}