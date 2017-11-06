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
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MainActivity
import com.sharpdroid.registroelettronico.api.v2.APIClient
import com.sharpdroid.registroelettronico.database.entities.LoginRequest
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Metodi.getStartEnd
import java.util.*

class NotificationService : JobService() {
    override fun onStopJob(job: JobParameters?): Boolean {
        return false //need retry?
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        val profile = Profile.getProfile(applicationContext) ?: return false
        val notificationsList = mutableMapOf<String, Int>()
        if (profile.expire.time < System.currentTimeMillis()) {
            APIClient.with(profile).postLoginBlocking(LoginRequest(profile.password, profile.username, profile.ident)).blockingSubscribe({ login ->
                if (!login.isSuccessful) return@blockingSubscribe

                profile.token = login.body()?.token ?: throw IllegalStateException("token not in response body")
                profile.expire = login.body()?.expire ?: Date(0)

                DatabaseHelper.database.profilesDao().update(profile)
            }, {
                it.printStackTrace()
            })
        }

        var diff = getAgendaDiff(profile)
        if (diff != 0)
            notificationsList.put("agenda", diff)
        Log.d("NOTIFICATION", "AGENDA - $diff")

        diff = getVotiDiff(profile)
        if (diff != 0)
            notificationsList.put("voti", diff)
        Log.d("NOTIFICATION", "VOTI - $diff")


        diff = getComunicazioniDiff(profile)
        if (diff != 0)
            notificationsList.put("comunicazioni", diff)
        Log.d("NOTIFICATION", "COMUNICAZIONI - $diff")

        diff = getNoteDiff(profile)
        if (diff != 0)
            notificationsList.put("note", diff)
        Log.d("NOTIFICATION", "NOTE - $diff")


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
        val agenda = mutableListOf<RemoteAgenda>()

        APIClient.with(profile).getAgendaBlocking(dates[0], dates[1]).blockingSubscribe({
            if (it.isSuccessful) {
                agenda.addAll(it.body()?.getAgenda(profile).orEmpty())
            } else {
                Log.d("NOTIFICATION", "agenda response was not successful")
            }
        }, {
            Log.d("NOTIFICATION", it.localizedMessage)
        })

        val diff = agenda.size - DatabaseHelper.database.query("SELECT ID FROM REMOTE_AGENDA WHERE PROFILE=?", arrayOf(profile.id)).count
        DatabaseHelper.database.eventsDao().deleteRemote(profile.id)
        DatabaseHelper.database.eventsDao().insert(agenda)
        return if (diff < 0) 0 else diff
    }

    private fun getVotiDiff(profile: Profile): Int {/*
        val marks: List<Grade> = try {
            //APIClient.with(profile).getGrades().blockingFirst()?.getGrades(profile) ?: return 0
            val response = APIClient.with(profile).getGradesBlocking().blockingFirst()
            if (response.isSuccessful) response.body()?.getGrades(profile) ?: return 0 else return 0
        } catch (e: IOException) {
            if (!BuildConfig.DEBUG)
                Answers.getInstance().logCustom(CustomEvent("IOException getVotiDiff").putCustomAttribute("stacktrace", e.localizedMessage))
            return 0
        }
        val diff = marks.size - SugarRecord.count<Grade>(Grade::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
        SugarRecord.deleteAll(Grade::class.java, "PROFILE=?", profile.id.toString())
        marks.forEach { it.profile = profile.id }
        SugarRecord.saveInTx(marks)
        return if (diff < 0) 0 else diff*/
        return 0
    }

    private fun getComunicazioniDiff(profile: Profile): Int {/*
        var comm: List<Communication> = try {
            //APIClient.with(profile).getBacheca().blockingFirst()?.getCommunications(profile) ?: return 0
            val response = APIClient.with(profile).getBachecaBlocking().blockingFirst()
            if (response.isSuccessful) response.body()?.getCommunications(profile) ?: return 0 else return 0
        } catch (e: IOException) {
            if (!BuildConfig.DEBUG)
                Answers.getInstance().logCustom(CustomEvent("IOException getComunicazioniDiff").putCustomAttribute("stacktrace", e.localizedMessage))
            return 0
        }
        comm = comm.filter { it.cntStatus == "deleted" }
        val diff = comm.size - SugarRecord.count<Communication>(Communication::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
        SugarRecord.deleteAll(Communication::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(comm)
        return if (diff < 0) 0 else diff*/
        return 0
    }

    private fun getNoteDiff(profile: Profile): Int {/*
        val note: List<Note> = try {
            //APIClient.with(profile).getNotes().blockingFirst()?.getNotes(profile) ?: return 0
            val response = APIClient.with(profile).getNotesBlocking().blockingFirst()
            if (response.isSuccessful) response.body()?.getNotes(profile) ?: return 0 else return 0
        } catch (e: IOException) {
            if (!BuildConfig.DEBUG)
                Answers.getInstance().logCustom(CustomEvent("IOException getNoteDiff").putCustomAttribute("stacktrace", e.localizedMessage))
            return 0
        }
        val diff = note.size - SugarRecord.count<Note>(Note::class.java, "PROFILE=?", arrayOf(profile.id.toString())).toInt()
        SugarRecord.deleteAll(Note::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(note)
        return if (diff < 0) 0 else diff*/
        return 0
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