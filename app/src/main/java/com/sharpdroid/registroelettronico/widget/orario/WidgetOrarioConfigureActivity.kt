package com.sharpdroid.registroelettronico.widget.orario

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import kotlinx.android.synthetic.main.widget_orario_configure.*

/**
 * The configuration screen for the [WidgetOrario] AppWidget.
 */
class WidgetOrarioConfigureActivity : Activity(), AdapterView.OnItemSelectedListener {
    val profiles by lazy { DatabaseHelper.database.profilesDao().profilesSync.sortedBy { it.id } }
    var selectedProfile: Profile? = null


    override fun onNothingSelected(p0: AdapterView<*>?) {
        selectedProfile = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedProfile = profiles[position]
    }

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    var configureListener: View.OnClickListener = View.OnClickListener {
        val context = this@WidgetOrarioConfigureActivity

        // User has not selected user
        if (selectedProfile == null) {
            finish()
            return@OnClickListener
        }

        // When the button is clicked, store the value locally
        saveProfilePref(context, mAppWidgetId, selectedProfile!!.id)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        WidgetOrario.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.widget_orario_configure)
        add_button.setOnClickListener(configureListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        spinner_profile.onItemSelectedListener = this
        spinner_profile.adapter = ArrayAdapter<Profile>(this, android.R.layout.simple_list_item_1, profiles)
    }


    companion object {

        private val PREFS_NAME = "com.sharpdroid.registroelettronico.widget.orario.WidgetOrario"
        private val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveProfilePref(context: Context, appWidgetId: Int, profile: Long) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putLong(PREF_PREFIX_KEY + appWidgetId, profile)
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadProfilePref(context: Context, appWidgetId: Int): Long {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, 0L)
        }

        internal fun deleteProfilePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }
}

