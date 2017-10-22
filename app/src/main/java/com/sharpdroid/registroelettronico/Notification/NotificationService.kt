package com.sharpdroid.registroelettronico.Notification

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.API.V2.APIClient
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

        var notificationsList = mutableMapOf<String, Int>()

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

        notify(notificationsList)

        return false //something else to do?
    }

    private fun notify(notificationsList: MutableMap<String, Int>) {
        if (notificationsList.keys.isEmpty()) return

        var title: String = ""
        if (notificationsList.keys.size == 1) {
            when (notificationsList.keys.toTypedArray()[0]) {
                "agenda" -> title = applicationContext.resources.getQuantityString(R.plurals.notification_agenda, notificationsList["agenda"]!!, notificationsList["agenda"]!!)
                "voti" -> title = applicationContext.resources.getQuantityString(R.plurals.notification_voti, notificationsList["voti"]!!, notificationsList["voti"]!!)
                "comunicazioni" -> title = applicationContext.resources.getQuantityString(R.plurals.notification_communication, notificationsList["comunicazioni"]!!, notificationsList["comunicazioni"]!!)
                "note" -> title = applicationContext.resources.getQuantityString(R.plurals.notification_communication, notificationsList["note"]!!, notificationsList["note"]!!)
            }
        } else {
            title = "Ci sono novità!"
        }

        println(title)

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
/*
    @RequiresApi(26)
    private fun checkUpdatesV26(context: Context, preferences: SharedPreferences, last_item_key_name: String, notify: Boolean) {
        if (!notify) return

        val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
        val mBuilder: Notification.Builder

        val title = "Quadri - Circolari"
        val content = "Nuove circolari da leggere"
        val i = Intent(context, MainActivity::class.java)
        i.putExtra("drawer_to_open", R.id.)
        val intent = PendingIntent.getActivity(context, MainActivity.REQUEST_CODE, i, 0)

        mBuilder = Notification.Builder(context, if (preferences.getBoolean("notify_sound", true)) channelId else channelId_mute)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentText(content)
                .setContentTitle(title)
                .setContentIntent(intent)
                .setAutoCancel(true)


        val channel = NotificationChannel(channelId, "iQuadri", NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.enableVibration(preferences.getBoolean("notify_vibrate", true))
        channel.lightColor = Color.BLUE

        val channelMute = NotificationChannel(channelId_mute, "iQuadri silent", NotificationManager.IMPORTANCE_LOW)
        channelMute.enableLights(true)
        channelMute.enableVibration(preferences.getBoolean("notify_vibrate", true))
        channelMute.lightColor = Color.BLUE

        if (preferences.getBoolean("notify_vibrate", true)) {
            channel.vibrationPattern = longArrayOf(250, 250, 250, 250)
        }
        if (preferences.getBoolean("notify_sound", true)) {
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_NOTIFICATION).build())
        }

        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(channelMute)
        notificationManager.notify(nNotif, mBuilder.build())

        //PreferenceManager.getDefaultSharedPreferences(context).edit().putString(last_item_key_name, firstItem.title.toLowerCase().trim { it <= ' ' }).apply()
    }

    private fun checkUpdates(context: Context, firstItem: Circolare, preferences: SharedPreferences, last_item_key_name: String, notify: Boolean) {
        if (!notify) return
        if (!BuildConfig.DEBUG && firstItem.title.toLowerCase().trim() != preferences.getString(last_item_key_name, "").toLowerCase().trim()) return

        Log.w("CircolariService", "Shoot Notification -> " + firstItem.title)

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        val mBuilder: NotificationCompat.Builder

        val title = "Quadri - Circolari"
        val content = "Nuove circolari da leggere"
        val i = Intent(context, ActivityMain::class.java)
        i.putExtra("tab", R.id.tab_circolari)
        val intent = PendingIntent.getActivity(context, ActivityMain.CIRCOLARI_ID, i, 0)

        mBuilder = NotificationCompat.Builder(context, "iQuadri")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentText(content)
                .setContentTitle(title)
                .setContentIntent(intent)
                .setLights(Color.BLUE, 3000, 3000)
                .setAutoCancel(true)

        if (preferences.getBoolean("notify_vibrate", true))
            mBuilder.setVibrate(longArrayOf(250, 250, 250, 250))
        if (preferences.getBoolean("notify_sound", true))
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        notificationManager.notify(nNotif, mBuilder.build())

        //PreferenceManager.getDefaultSharedPreferences(context).edit().putString(last_item_key_name, firstItem.title.toLowerCase().trim { it <= ' ' }).apply()
    }*/

    companion object {
        private const val nNotif: Int = 977
        private val last_circolare = "last_circolare"
        private val channelId = "iquadri_channel_01"
        private val channelId_mute = "iquadri_channel_02"
    }
}