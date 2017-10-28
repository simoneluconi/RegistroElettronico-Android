package com.sharpdroid.registroelettronico.notification

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
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MainActivity
import com.sharpdroid.registroelettronico.api.v2.APIClient
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.utils.Metodi.getStartEnd

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
            Log.d("NOTIFICATION", "AGENDA - $diff")
        }
        if (option.notifyVoti) {
            val diff = getVotiDiff(profile)
            if (diff != 0)
                notificationsList.put("voti", diff)
            Log.d("NOTIFICATION", "VOTI - $diff")
        }
        if (option.notifyComunicazioni) {
            val diff = getComunicazioniDiff(profile)
            if (diff != 0)
                notificationsList.put("comunicazioni", diff)
            Log.d("NOTIFICATION", "COMUNICAZIONI - $diff")
        }
        if (option.notifyNote) {
            val diff = getNoteDiff(profile)
            if (diff != 0)
                notificationsList.put("note", diff)
            Log.d("NOTIFICATION", "NOTE - $diff")
        }

        notify(notificationsList, PreferenceManager.getDefaultSharedPreferences(this))

        return false //something else to do?
    }

    private fun notify(notificationsList: MutableMap<String, Int>, preferences: SharedPreferences) {
        if (notificationsList.keys.isEmpty()) return

        val sound = preferences.getBoolean("notify_sound", true)
        val vibrate = preferences.getBoolean("notify_vibrate", true)
        val content = resources.getString(R.string.click_to_open)

        if (notificationsList.keys.size == 1) {
            when (notificationsList.keys.toTypedArray()[0]) {
                "agenda" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_agenda, notificationsList["agenda"]!!, notificationsList["agenda"]!!), content, sound, vibrate, R.id.agenda.toLong())
                }
                "voti" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_voti, notificationsList["voti"]!!, notificationsList["voti"]!!), content, sound, vibrate, R.id.medie.toLong())
                }
                "comunicazioni" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_communication, notificationsList["comunicazioni"]!!, notificationsList["comunicazioni"]!!), content, sound, vibrate, R.id.communications.toLong())
                }
                "note" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_note, notificationsList["note"]!!, notificationsList["note"]!!), content, sound, vibrate, R.id.notes.toLong())
                }
            }
        } else {
            pushNotification(resources.getString(R.string.new_news), content, sound, vibrate, null)
            return
        }
    }

    private fun getAgendaDiff(profile: Profile): Int {
        val dates = getStartEnd("yyyyMMdd")
        val agenda: List<RemoteAgenda>
        agenda = try {
            APIClient.with(applicationContext, profile).getAgenda(dates[0], dates[1]).blockingFirst()?.getAgenda(profile) ?: return 0
        } catch (err: Error) {
            return 0
        }
        val diff = agenda.size - SugarRecord.count<RemoteAgenda>(RemoteAgenda::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
        SugarRecord.deleteAll(RemoteAgenda::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(agenda)
        return if (diff < 0) 0 else diff
    }

    private fun getVotiDiff(profile: Profile): Int {
        val marks: List<Grade> = try {
            APIClient.with(applicationContext, profile).getGrades().blockingFirst()?.getGrades(profile) ?: return 0
        } catch (e: Error) {
            return 0
        }
        val diff = marks.size - SugarRecord.count<Grade>(Grade::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
        SugarRecord.deleteAll(Grade::class.java, "PROFILE=?", profile.id.toString())
        marks.forEach { it.profile = profile.id }
        SugarRecord.saveInTx(marks)
        return if (diff < 0) 0 else diff
    }

    private fun getComunicazioniDiff(profile: Profile): Int {
        var comm: List<Communication> = try {
            APIClient.with(applicationContext, profile).getBacheca().blockingFirst()?.getCommunications(profile) ?: return 0
        } catch (e: Error) {
            return 0
        }
        comm = comm.filter { it.cntStatus == "deleted" }
        val diff = comm.size - SugarRecord.count<Communication>(Communication::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
        SugarRecord.deleteAll(Communication::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(comm)
        return if (diff < 0) 0 else diff
    }

    private fun getNoteDiff(profile: Profile): Int {
        val note: List<Note> = try {
            APIClient.with(applicationContext, profile).getNotes().blockingFirst()?.getNotes(profile) ?: return 0
        } catch (e: Error) {
            return 0
        }
        val diff = note.size - SugarRecord.count<Note>(Note::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
        SugarRecord.deleteAll(Note::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(note)
        return if (diff < 0) 0 else diff
    }

    private fun pushNotification(title: String, content: String?, sound: Boolean, vibrate: Boolean, tabToOpen: Long?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            val mBuilder: Notification.Builder

            val i = Intent(this, MainActivity::class.java)
            if (tabToOpen != null) {
                val bundle = Bundle()
                bundle.putLong("drawer_open_id", tabToOpen)
                i.putExtras(bundle)
            }
            val intent = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)

            mBuilder = Notification.Builder(this, if (sound) channelId else channelId_mute)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
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
            if (tabToOpen != null) {
                val bundle = Bundle()
                bundle.putLong("drawer_open_id", tabToOpen)
                i.putExtras(bundle)
            }
            val intent = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)

            mBuilder = NotificationCompat.Builder(this, "Registro Elettronico")
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
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