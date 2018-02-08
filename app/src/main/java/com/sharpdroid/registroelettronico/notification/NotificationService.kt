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
import com.sharpdroid.registroelettronico.api.spaggiari.v2.Spaggiari
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.fragments.FragmentAgenda
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.getStartEnd
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class NotificationService : JobService() {
    private val debug = false

    override fun onStopJob(job: JobParameters?) = false //need retry?

    override fun onStartJob(job: JobParameters?): Boolean {
        val profile = Profile.getProfile(applicationContext) ?: return false
        if (profile.expire.time < System.currentTimeMillis()) {
            var successful = false
            var login: LoginResponse? = null
            Spaggiari(profile).api().postLoginBlocking(LoginRequest(profile.password, profile.username, profile.ident)).blockingSubscribe({
                successful = it?.isSuccessful == true
                login = it.body()
            }, {
                Log.e("NotificationService", it?.localizedMessage, it)
            })
            if (!successful) return false

            profile.token = login?.token ?: throw IllegalStateException("token not in response body")
            profile.expire = login?.expire ?: Date(0)

            DatabaseHelper.database.profilesDao().update(profile)
        }

        val notificationsList = mutableMapOf<NotificationIDs, List<Any>>()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        var diff: List<Any>

        if (preferences.getBoolean("notify_agenda", true)) {
            diff = getAgendaDiff(profile)
            if (diff.isNotEmpty())
                notificationsList[NotificationIDs.AGENGA] = diff
            Log.d("NotificationService", "${diff.size} nuovi eventi")
        }
        if (preferences.getBoolean("notify_voti", true)) {
            diff = getVotiDiff(profile)
            if (diff.isNotEmpty())
                notificationsList[NotificationIDs.VOTI] = diff
            Log.d("NotificationService", "${diff.size} nuovi voti")
        }
        if (preferences.getBoolean("notify_comunicazioni", true)) {
            diff = getComunicazioniDiff(profile)
            if (diff.isNotEmpty())
                notificationsList[NotificationIDs.COMUNICAZIONI] = diff
            Log.d("NotificationService", "${diff.size} nuove comunicazioni")
        }
        if (preferences.getBoolean("notify_note", true)) {
            diff = getNoteDiff(profile)
            if (diff.isNotEmpty())
                notificationsList[NotificationIDs.NOTE] = diff
            Log.d("NotificationService", "${diff.size} nuove note")
        }

        notify(notificationsList, preferences)

        return false //something else to do?
    }

    private fun notify(notificationsList: Map<NotificationIDs, List<Any>>, preferences: SharedPreferences) {
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

    private fun pushNotification(title: String, type: NotificationIDs, content: List<Any>, sound: Boolean, vibrate: Boolean, tabToOpen: Long) {
        val notificationManager = NotificationManagerCompat.from(this)

        val intent = Intent(this, MainActivity::class.java)
                .putExtra("drawer_open_id", tabToOpen)
                .putExtra("list", content as Serializable)
                .addCategory(type.name)

        if (content.size == 1) {
            with(content[0]) {
                if (this is RemoteAgenda) {
                    intent.putExtra(FragmentAgenda.INTENT_DATE, start.time)
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(this, MainActivity.REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val saveDataIntent = Intent(this, NotificationReceiver::class.java)
                .setAction(NotificationReceiver.ACTION_NOTIFICATION_DISMISSED)
                .putExtra("list", content as Serializable)
                .addCategory(type.name)
        val saveDataPendingIntent = PendingIntent.getBroadcast(this, MainActivity.REQUEST_CODE, saveDataIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val style = NotificationCompat.InboxStyle()

        content.forEach {
            style.addLine(getCaption(it))
        }

        val caption = getCaption(content.first())

        val notification = NotificationCompat.Builder(this, type.name)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setContentIntent(pendingIntent)
                .setContentText(caption)
                .setContentTitle(title)
                .setDeleteIntent(saveDataPendingIntent)
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

    private fun getCaption(item: Any): String {
        return when (item) {
            is RemoteAgenda -> " ${dateFormat.format(item.start)} - ${item.notes}"           // 5 feb - Verifica di informatica
            is Grade -> getString(R.string.notification_new_grade, item.mStringValue, capitalizeEach(item.mDescription, false)) //Hai preso $x in $y
            is Communication -> item.title              // Circ n.7...
            is Note -> "${capitalizeEach(item.mAuthor, true)} - ${item.mText}"  //Prof Annunzio - Alunno rompe il cazzo
            else -> ""
        }
    }

    private fun getAgendaDiff(profile: Profile): List<RemoteAgenda> {
        val dates = getStartEnd("yyyyMMdd")

        var newEvents = emptyList<RemoteAgenda>()
        Spaggiari(profile).api().getAgendaBlocking(dates[0], dates[1]).blockingSubscribe({
            if (it?.isSuccessful == true) {
                newEvents = it.body()?.getAgenda(profile) ?: emptyList()
            } else {
                Log.e("NotificationService", "agenda response not successful")
            }
        }, {
            Log.e("NotificationService", it?.localizedMessage, it)
        })
        if (newEvents.isEmpty()) return emptyList()

        val oldEvents = DatabaseHelper.database.eventsDao().getRemoteList(profile.id)
        val diffEvents = newEvents.minus(if (!debug) oldEvents else oldEvents.dropLast(1))
        if (diffEvents.isEmpty()) return emptyList()
        return diffEvents
    }

    private fun getVotiDiff(profile: Profile): List<Grade> {
        var newGrades = emptyList<Grade>()
        Spaggiari(profile).api().getGradesBlocking().blockingSubscribe({
            if (it?.isSuccessful == true) {
                newGrades = it.body()?.getGrades(profile) ?: emptyList()
            } else {
                Log.e("NotificationService", "grade response not successful")
            }
        }, {
            Log.e("NotificationService", it?.localizedMessage, it)
        })
        if (newGrades.isEmpty()) return emptyList()

        val oldGrades = DatabaseHelper.database.gradesDao().getGradesList(profile.id)
        val diffGrades = newGrades.minus(oldGrades)
        if (diffGrades.isEmpty()) return emptyList()
        return diffGrades
    }

    private fun getComunicazioniDiff(profile: Profile): List<Communication> {
        var newCommunications = emptyList<Communication>()
        Spaggiari(profile).api().getBachecaBlocking().blockingSubscribe({
            if (it?.isSuccessful == true) {
                newCommunications = it.body()?.getCommunications(profile) ?: emptyList()
            } else {
                Log.e("NotificationService", "communication response not successful")
            }
        }, {
            Log.e("NotificationService", it?.localizedMessage, it)
        })
        newCommunications = newCommunications.filter { it.cntStatus != "deleted" }
        newCommunications.map { it.cntStatus = "" }
        if (newCommunications.isEmpty()) return emptyList()

        val oldCommunications = DatabaseHelper.database.communicationsDao().getCommunicationsList(profile.id)
        val diffCommunications = newCommunications.minus(oldCommunications)
        if (diffCommunications.isEmpty()) return emptyList()
        return diffCommunications
    }

    private fun getNoteDiff(profile: Profile): List<Note> {
        var newNotes = emptyList<Note>()
        Spaggiari(profile).api().getNotesBlocking().blockingSubscribe({
            if (it?.isSuccessful == true) {
                newNotes = it.body()?.getNotes(profile) ?: emptyList()
            } else {
                Log.e("NotificationService", "note response not successful")
            }
        }, {
            Log.e("NotificationService", it?.localizedMessage, it)
        })
        if (newNotes.isEmpty()) return emptyList()

        val oldNotes = DatabaseHelper.database.notesDao().getNotesList(profile.id)
        val diffNotes = newNotes.minus(oldNotes)
        if (diffNotes.isEmpty()) return emptyList()
        return diffNotes
    }

    companion object {
        enum class NotificationIDs {
            AGENGA,
            VOTI,
            COMUNICAZIONI,
            NOTE
        }

        val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
    }
}