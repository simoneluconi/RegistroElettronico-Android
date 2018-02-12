package com.sharpdroid.registroelettronico.widget.orario

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.sharpdroid.registroelettronico.R

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [WidgetOrarioConfigureActivity]
 */
class WidgetOrario : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            WidgetOrarioConfigureActivity.deleteProfilePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val widgetProfileId = WidgetOrarioConfigureActivity.loadProfilePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_orario)

            val i = Intent(context, WidgetOrarioService::class.java)
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            i.data = Uri.parse(i.toUri(Intent.URI_INTENT_SCHEME))
            i.putExtra("profile", widgetProfileId)
            views.setRemoteAdapter(R.id.orario_remote_list, i)
            views.setEmptyView(R.id.orario_remote_list, R.id.empty)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

