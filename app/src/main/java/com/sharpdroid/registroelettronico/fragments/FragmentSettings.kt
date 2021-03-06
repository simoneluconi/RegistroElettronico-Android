package com.sharpdroid.registroelettronico.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.FragmentTransaction
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceCategory
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.TwoStatePreference
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.notification.NotificationIDs
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.pushDatabase
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * shows the settings option for choosing the movie categories in ListPreference.
 */
class FragmentSettings : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        //add xml
        addPreferencesFromResource(R.xml.preferences)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        onSharedPreferenceChanged(sharedPreferences, "voto_obiettivo")
        onSharedPreferenceChanged(sharedPreferences, "drawer_to_open")
        onSharedPreferenceChanged(sharedPreferences, "notify")
        onSharedPreferenceChanged(sharedPreferences, "notify_sound")
        onSharedPreferenceChanged(sharedPreferences, "notify_vibrate")

        findPreference("clear_archive").setOnPreferenceClickListener { _ ->
            DatabaseHelper.database.eventsDao().setLocalNotArchived()
            DatabaseHelper.database.eventsDao().setRemoteNotArchived()
            pushDatabase(context)
            return@setOnPreferenceClickListener true
        }

        findPreference("voto_obiettivo").setOnPreferenceClickListener { _ ->
            DatabaseHelper.database.subjectsDao().removeTargets(Account.with(context).user)
            return@setOnPreferenceClickListener true
        }

        findPreference("excluded_marks").setOnPreferenceClickListener { _ ->
            DatabaseHelper.database.gradesDao().cleanExcludedMarks(Account.with(context).user)
            return@setOnPreferenceClickListener true
        }

        findPreference("credits").setOnPreferenceClickListener { _ ->
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragment_container, FragmentCredits()).addToBackStack(null)
            transaction.commit()
            return@setOnPreferenceClickListener true
        }

        findPreference("classe").setOnPreferenceClickListener { preference ->
            Completable.fromAction {
                val profile = Profile.getProfile(context)
                profile?.classe = (preference as ListPreference).value.orEmpty()

                if (profile != null) {
                    DatabaseHelper.database.profilesDao().update(profile)
                }
            }.subscribeOn(Schedulers.computation())
            return@setOnPreferenceClickListener true
        }

        val preference = findPreference("notifiche_category") as PreferenceCategory
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            preference.removePreference(findPreference("notify"))
            preference.removePreference(findPreference("notify_sound"))
            preference.removePreference(findPreference("notify_vibrate"))
            preference.removePreference(findPreference("notify_agenda"))
            preference.removePreference(findPreference("notify_voti"))
            preference.removePreference(findPreference("notify_comunicazioni"))
            preference.removePreference(findPreference("notify_note"))

            findPreference("notify_settings").setOnPreferenceClickListener {
                for (type in NotificationIDs.values()) {
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

                    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
                }

                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                startActivity(intent)
                return@setOnPreferenceClickListener true
            }
        } else {
            preference.removePreference(findPreference("notify_settings"))
        }
    }

    override fun onResume() {
        super.onResume()
        //unregister the preferenceChange listener
        activity.setTitle(R.string.settings)
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference(key) ?: return

        if (preference is ListPreference) {
            val prefIndex = preference.findIndexOfValue(sharedPreferences.getString(key, ""))
            if (prefIndex >= 0) {
                preference.setSummary(preference.entries[prefIndex])
            }
        } else if (preference is TwoStatePreference) {
            if (key.equals("notify", ignoreCase = true)) {
                val vibrate = findPreference("notify_vibrate")?.icon ?: return
                vibrate.alpha = if (preference.isChecked) 255 else 102
                findPreference("notify_vibrate").icon = vibrate

                val sound = findPreference("notify_sound")?.icon ?: return
                sound.alpha = if (preference.isChecked) 255 else 102
                findPreference("notify_sound").icon = sound
            }
        } else {
            preference.summary = sharedPreferences.getString(key, "")
        }
    }

    override fun onPause() {
        super.onPause()
        //unregister the preference change listener
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }
}