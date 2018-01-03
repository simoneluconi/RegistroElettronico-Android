package com.sharpdroid.registroelettronico.fragments

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceCategory
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.TwoStatePreference
import android.view.View
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.PushDatabase
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * shows the settings option for choosing the movie categories in ListPreference.
 */
class FragmentSettings : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {

        PushDatabase(context)

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
            true
        }

        findPreference("voto_obiettivo").setOnPreferenceClickListener { _ ->
            DatabaseHelper.database.subjectsDao().removeTargets(Account.with(context).user)
            true
        }


        findPreference("credits").setOnPreferenceClickListener { _ ->
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragment_container, FragmentCredits()).addToBackStack(null)
            transaction.commit()
            true
        }

        findPreference("classe").setOnPreferenceClickListener { preference ->
            Completable.fromAction {
                val profile = Profile.getProfile(context)
                profile?.classe = (preference as ListPreference).value.orEmpty()

                if (profile != null) {
                    DatabaseHelper.database.profilesDao().update(profile)
                }
            }.subscribeOn(Schedulers.computation())
            true
        }

        findPreference("attribution").setOnPreferenceClickListener { _ ->
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragment_container, LibsBuilder().withUiListener(object : LibsConfiguration.LibsUIListener {
                override fun preOnCreateView(view: View) = view

                override fun postOnCreateView(view: View): View {
                    if (activity != null)
                        activity.title = "Attribuzioni"
                    return view
                }
            }).supportFragment()).addToBackStack(null)
            transaction.commit()
            true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val preference = findPreference("notifuche_preference") as PreferenceCategory
            preference.removePreference(findPreference("notify_sound"))
            preference.removePreference(findPreference("notify_vibrate"))
        }

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Impostazioni"))
    }

    override fun onResume() {
        super.onResume()
        //unregister the preferenceChange listener
        activity.setTitle(R.string.settings)
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference(key)
        if (preference is ListPreference) {
            val prefIndex = preference.findIndexOfValue(sharedPreferences.getString(key, ""))
            if (prefIndex >= 0) {
                preference.setSummary(preference.entries[prefIndex])
            }
        } else if (preference is TwoStatePreference) {
            if (key.equals("notify", ignoreCase = true)) {
                val vibrate = findPreference("notify_vibrate").icon
                vibrate.alpha = if (preference.isChecked) 255 else 102
                findPreference("notify_vibrate").icon = vibrate

                val sound = findPreference("notify_sound").icon
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