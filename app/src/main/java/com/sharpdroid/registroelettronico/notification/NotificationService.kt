package com.sharpdroid.registroelettronico.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MainActivity
import com.sharpdroid.registroelettronico.api.v2.APIClient
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.getStartEnd
import java.util.*

class NotificationService : JobService() {
    private val debug = false

    override fun onStopJob(job: JobParameters?) = false //need retry?

    override fun onStartJob(job: JobParameters?): Boolean {
        val profile = Profile.getProfile(applicationContext) ?: return false
        if (profile.expire.time < System.currentTimeMillis()) {
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
            profile.expire = login?.expire ?: Date(0)

            DatabaseHelper.database.profilesDao().update(profile)
        }

        val notificationsList = mutableMapOf<NotificationIDs, List<String>>()

        var diff: List<String>
        diff = getAgendaDiff(profile)
        if (diff.isNotEmpty())
            notificationsList.put(NotificationIDs.AGENGA, diff)
        Log.d("NOTIFICATION", "AGENDA - ${diff.size}")

        diff = getVotiDiff(profile)
        if (diff.isNotEmpty())
            notificationsList.put(NotificationIDs.VOTI, diff)
        Log.d("NOTIFICATION", "VOTI - ${diff.size}")

        diff = getComunicazioniDiff(profile)
        if (diff.isNotEmpty())
            notificationsList.put(NotificationIDs.COMUNICAZIONI, diff)
        Log.d("NOTIFICATION", "COMUNICAZIONI - ${diff.size}")

        diff = getNoteDiff(profile)
        if (diff.isNotEmpty())
            notificationsList.put(NotificationIDs.NOTE, diff)
        Log.d("NOTIFICATION", "NOTE - ${diff.size}")

        notify(notificationsList, PreferenceManager.getDefaultSharedPreferences(this))

        return false //something else to do?
    }

    private fun notify(notificationsList: Map<NotificationIDs, List<String>>, preferences: SharedPreferences) {
        if (notificationsList.isEmpty()) return

        val sound = preferences.getBoolean("notify_sound", true)
        val vibrate = preferences.getBoolean("notify_vibrate", true)

        notificationsList.forEach {
            when (it.key) {
                NotificationIDs.AGENGA -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_agenda, it.value.size, it.value.size), it.key, it.value, sound, vibrate, R.id.agenda.toLong())
                }
                NotificationIDs.VOTI -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_voti, it.value.size, it.value.size), it.key, it.value, sound, vibrate, R.id.medie.toLong())
                }
                NotificationIDs.COMUNICAZIONI -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_communication, it.value.size, it.value.size), it.key, it.value, sound, vibrate, R.id.communications.toLong())
                }
                NotificationIDs.NOTE -> {
                    pushNotification(resources.getQuantityString(R.plurals.notification_note, it.value.size, it.value.size), it.key, it.value, sound, vibrate, R.id.notes.toLong())
                }
            }
        }
    }

    private fun getAgendaDiff(profile: Profile): List<String> {
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
        if (newEvents.isEmpty()) return emptyList()

        val oldEvents = DatabaseHelper.database.eventsDao().getRemoteList(Account.with(applicationContext).user)
        val diffEvents = newEvents.minus(oldEvents)
        if (diffEvents.isEmpty()) return emptyList()
        return diffEvents.map { it.notes }
    }

    private fun getVotiDiff(profile: Profile): List<String> {
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
        if (newGrades.isEmpty()) return emptyList()

        val oldGrades = DatabaseHelper.database.gradesDao().getGradesList(Account.with(applicationContext).user)
        val diffGrades = if (!debug) newGrades.minus(oldGrades) else newGrades
        if (diffGrades.isEmpty()) return emptyList()
        return diffGrades.map { getString(R.string.notification_new_grade, it.mStringValue, capitalizeEach(it.mDescription, false)) }
    }

    private fun getComunicazioniDiff(profile: Profile): List<String> {
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
        newCommunications = newCommunications.filter { it.cntStatus != "deleted" }
        newCommunications.map { it.cntStatus = "" }
        if (newCommunications.isEmpty()) return emptyList()

        val oldCommunications = DatabaseHelper.database.communicationsDao().getCommunicationsList(Account.with(applicationContext).user)
        val diffCommunications = if (!debug) newCommunications.minus(oldCommunications) else newCommunications
        if (diffCommunications.isEmpty()) return emptyList()
        return diffCommunications.map { it.title }
    }

    private fun getNoteDiff(profile: Profile): List<String> {
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
        if (newNotes.isEmpty()) return emptyList()

        val oldNotes = DatabaseHelper.database.notesDao().getNotesList(Account.with(applicationContext).user)
        val diffNotes = newNotes.minus(oldNotes)
        if (diffNotes.isEmpty()) return emptyList()
        return diffNotes.map { it.mText }
    }

    private fun pushNotification(title: String, type: NotificationIDs, content: List<String>, sound: Boolean, vibrate: Boolean, tabToOpen: Long) {
        val notificationManager = NotificationManagerCompat.from(this)

        val intent = Intent(this, MainActivity::class.java)
                .putExtra("drawer_open_id", tabToOpen)
                .setAction(type.name)
        val pi = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val style = NotificationCompat.InboxStyle()

        content.forEach {
            style.addLine(it)
        }

        val notification = NotificationCompat.Builder(this, type.name)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setContentIntent(pi)
                .setContentText(content.first())
                .setContentTitle(title)
                .setNumber(content.size)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setStyle(style)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (vibrate)
                notification.setVibrate(longArrayOf(250, 250, 250, 250))
            if (sound)
                notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            notification.setLights(Color.BLUE, 3000, 3000)
        } else {
            val channelName = when (type) {
                NotificationIDs.AGENGA -> "Agenda"
                NotificationIDs.VOTI -> "Voti"
                NotificationIDs.COMUNICAZIONI -> "Comunicazioni"
                NotificationIDs.NOTE -> "Note"
            }

            val channel = NotificationChannel(type.name, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lightColor = Color.BLUE
            channel.setShowBadge(true)

            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        notificationManager.notify(type.ordinal, notification.build())
    }

    companion object {
        enum class NotificationIDs {
            AGENGA,
            VOTI,
            COMUNICAZIONI,
            NOTE
        }
    }
}