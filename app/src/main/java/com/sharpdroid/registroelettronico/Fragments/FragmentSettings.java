package com.sharpdroid.registroelettronico.Fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.TwoStatePreference;

import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.Databases.Entities.RemoteAgendaInfo;
import com.sharpdroid.registroelettronico.R;

/**
 * shows the settings option for choosing the movie categories in ListPreference.
 */
public class FragmentSettings extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = FragmentSettings.class.getSimpleName();

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        onSharedPreferenceChanged(sharedPreferences, "voto_obiettivo");
        onSharedPreferenceChanged(sharedPreferences, "drawer_to_open");
        onSharedPreferenceChanged(sharedPreferences, "notify");
        onSharedPreferenceChanged(sharedPreferences, "notify_sound");
        onSharedPreferenceChanged(sharedPreferences, "notify_vibrate");

        Preference button = findPreference("clear_archive");
        button.setOnPreferenceClickListener(preference -> {
            SugarRecord.deleteAll(RemoteAgendaInfo.class);
            SugarRecord.executeQuery("UPDATE LOCAL_AGENDA SET ARCHIVED=0 WHERE ARCHIVED!=0");
            return true;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getActivity().setTitle(R.string.settings);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof TwoStatePreference) {
            if (key.equalsIgnoreCase("notify")) {
                Drawable vibrate_d = findPreference("notify_vibrate").getIcon();
                vibrate_d.setAlpha(((TwoStatePreference) preference).isChecked() ? 255 : 102);
                findPreference("notify_vibrate").setIcon(vibrate_d);

                Drawable sound_d = findPreference("notify_sound").getIcon();
                sound_d.setAlpha(((TwoStatePreference) preference).isChecked() ? 255 : 102);
                findPreference("notify_sound").setIcon(sound_d);
            }
        } else {
            preference.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}