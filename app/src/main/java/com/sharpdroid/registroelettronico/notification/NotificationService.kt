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

        val notificationsList = mutableMapOf<String, Pair<List<String>, Int>>()
        if (profile.expire < System.currentTimeMillis()) {
            var successful = false
            var login: LoginResponse? = null
            APIClient.with(profile).postLoginBlocking(LoginRequest(profile.password, profile.username, profile.ident)).blockingSubscribe({
                successful = it?.isSuccessful == true
                login = it.body()
            }, {
                Log.e("NOTIFICATION", it?.localizedMessage, it)
            })
            if (!successful) return false

            profile.token = login?.token ?: throw IllegalStateException("token not in response body")
            profile.expire = login?.expire?.time ?: 0

            SugarRecord.update(profile)
        }

        if (option.notifyAgenda) {
            val diff = getAgendaDiff(profile)
            if (diff.second != 0)
                notificationsList.put("agenda", diff)
            Log.d("NOTIFICATION", "AGENDA - ${diff.second}")
        }

        if (option.notifyVoti) {
            val diff = getVotiDiff(profile)
            if (diff.second != 0)
                notificationsList.put("voti", diff)
            Log.d("NOTIFICATION", "VOTI - ${diff.second}")
        }

        if (option.notifyComunicazioni) {
            val diff = getComunicazioniDiff(profile)
            if (diff.second != 0)
                notificationsList.put("comunicazioni", diff)
            Log.d("NOTIFICATION", "COMUNICAZIONI - ${diff.second}")
        }

        if (option.notifyNote) {
            val diff = getNoteDiff(profile)
            if (diff.second != 0)
                notificationsList.put("note", diff)
            Log.d("NOTIFICATION", "NOTE - ${diff.second}")
        }

        notify(notificationsList, PreferenceManager.getDefaultSharedPreferences(this))

        return false //something else to do?
    }

    private fun notify(notificationsList: Map<String, Pair<List<String>, Int>>, preferences: SharedPreferences) {
        if (notificationsList.isEmpty()) return

        val sound = preferences.getBoolean("notify_sound", true)
        val vibrate = preferences.getBoolean("notify_vibrate", true)

        notificationsList.forEach {
            when (it.key) {
                "agenda" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_agenda, it.value.second, it.value.second), agenda_id, it.value.first, sound, vibrate, R.id.agenda.toLong())
                }
                "voti" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_voti, it.value.second, it.value.second), voti_id, it.value.first, sound, vibrate, R.id.medie.toLong())
                }
                "comunicazioni" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_communication, it.value.second, it.value.second), comunicazioni_id, it.value.first, sound, vibrate, R.id.communications.toLong())
                }
                "note" -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_note, it.value.second, it.value.second), note_id, it.value.first, sound, vibrate, R.id.notes.toLong())
                }
            }
        }
    }

    private fun getAgendaDiff(profile: Profile): Pair<List<String>, Int> {
        val dates = getStartEnd("yyyyMMdd")

        var newEvents = emptyList<RemoteAgenda>()
        APIClient.with(profile).getAgendaBlocking(dates[0], dates[1]).blockingSubscribe({
            if (it?.isSuccessful == true) {
                newEvents = it.body()?.getAgenda(profile) ?: emptyList()
            } else {
                Log.e("NOTIFICATION", "agenda response not successful")
            }
        }, {
            Log.e("NOTIFICATION", it?.localizedMessage, it)
        })
        if (newEvents.isEmpty()) return Pair(emptyList(), 0)

        val oldEvents = SugarRecord.find(RemoteAgenda::class.java, "PROFILE=?", profile.id.toString())
        val diffEvents = newEvents.minus(oldEvents)
        if (diffEvents.isEmpty()) return Pair(emptyList(), 0)

        SugarRecord.deleteAll(RemoteAgenda::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(newEvents)
        return Pair(diffEvents.map { it.notes }, diffEvents.size)
    }

    private fun getVotiDiff(profile: Profile): Pair<List<String>, Int> {
        var newGrades = emptyList<Grade>()
        APIClient.with(profile).getGradesBlocking().blockingSubscribe({
            if (it?.isSuccessful == true) {
                newGrades = it.body()?.getGrades(profile) ?: emptyList()
            } else {
                Log.e("NOTIFICATION", "grade response not successful")
            }
        }, {
            Log.e("NOTIFICATION", it?.localizedMessage, it)
        })
        if (newGrades.isEmpty()) return Pair(emptyList(), 0)

        val oldGrades = SugarRecord.find(Grade::class.java, "PROFILE=?", profile.id.toString())
        newGrades.map { it.profile = profile.id }
        val diffGrades = newGrades.minus(oldGrades)
        if (diffGrades.isEmpty()) return Pair(emptyList(), 0)

        SugarRecord.deleteAll(Grade::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(newGrades)
        return Pair(diffGrades.map { getString(R.string.notification_new_grade, it.mStringValue, it.mDescription) }, diffGrades.size)
    }

    private fun getComunicazioniDiff(profile: Profile): Pair<List<String>, Int> {
        var newCommunications = emptyList<Communication>()
        APIClient.with(profile).getBachecaBlocking().blockingSubscribe({
            if (it?.isSuccessful == true) {
                newCommunications = it.body()?.getCommunications(profile) ?: emptyList()
            } else {
                Log.e("NOTIFICATION", "communication response not successful")
            }
        }, {
            Log.e("NOTIFICATION", it?.localizedMessage, it)
        })
        if (newCommunications.isEmpty()) return Pair(emptyList(), 0)

        val oldCommunications = SugarRecord.find(Communication::class.java, "PROFILE=?", profile.id.toString())
        newCommunications = newCommunications.filter { it.cntStatus != "deleted" }
        newCommunications.map { it.cntStatus = "" }
        val diffCommunications = newCommunications.minus(oldCommunications)
        if (diffCommunications.isEmpty()) return Pair(emptyList(), 0)

        SugarRecord.deleteAll(Communication::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(newCommunications)
        return Pair(diffCommunications.map { it.title }, diffCommunications.size)
    }

    private fun getNoteDiff(profile: Profile): Pair<List<String>, Int> {
        var newNotes = emptyList<Note>()
        APIClient.with(profile).getNotesBlocking().blockingSubscribe({
            if (it?.isSuccessful == true) {
                newNotes = it.body()?.getNotes(profile) ?: emptyList()
            } else {
                Log.e("NOTIFICATION", "note response not successful")
            }
        }, {
            Log.e("NOTIFICATION", it?.localizedMessage, it)
        })
        if (newNotes.isEmpty()) return Pair(emptyList(), 0)

        val oldNotes = SugarRecord.find(Note::class.java, "PROFILE=?", profile.id.toString())
        val diffNotes = newNotes.minus(oldNotes)
        if (diffNotes.isEmpty()) return Pair(emptyList(), 0)

        SugarRecord.deleteAll(Note::class.java, "PROFILE=?", profile.id.toString())
        SugarRecord.saveInTx(newNotes)
        return Pair(diffNotes.map { it.mText }, diffNotes.size)
    }

    private fun pushNotification(title: String, type: Int, content: List<String>, sound: Boolean, vibrate: Boolean, tabToOpen: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            val mBuilder: Notification.Builder

            val i = Intent(this, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putLong("drawer_open_id", tabToOpen)
            i.putExtras(bundle)
            val intent = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)

            val style = Notification.InboxStyle()

            content.forEach {
                style.addLine(it)
            }

            mBuilder = Notification.Builder(this, if (sound) channelId else channelId_mute)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentIntent(intent)
                    .setStyle(style)
                    .setAutoCancel(true)

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
            notificationManager.notify(type, mBuilder.build())
        } else {
            val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
            val mBuilder: NotificationCompat.Builder
            val i = Intent(this, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putLong("drawer_open_id", tabToOpen)
            i.putExtras(bundle)
            val intent = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)

            val style = NotificationCompat.InboxStyle()

            content.forEach {
                style.addLine(it)
            }

            mBuilder = NotificationCompat.Builder(this, "Registro Elettronico")
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentIntent(intent)
                    .setStyle(style)
                    .setLights(Color.BLUE, 3000, 3000)
                    .setAutoCancel(true)

            if (vibrate)
                mBuilder.setVibrate(longArrayOf(250, 250, 250, 250))
            if (sound)
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            notificationManager.notify(type, mBuilder.build())
        }
    }

    companion object {
        private val channelId = "sharpdroid_registro_channel_01"
        private val channelId_mute = "sharpdroid_registro_channel_02"
        private val agenda_id = 400
        private val voti_id = 401
        private val comunicazioni_id = 402
        private val note_id = 403
    }
}
