package com.sharpdroid.registroelettronico.widget.agenda

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.sharpdroid.registroelettronico.R

/**
 * This widget shows current profile's events for the next few days.
 */
class WidgetAgenda : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
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
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_agenda_layout)

            // Setup the intent that will populate the list
            val i = Intent(context, WidgetAgendaService::class.java)
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            i.data = Uri.parse(i.toUri(Intent.URI_INTENT_SCHEME))

            // Start the adapter
            views.setRemoteAdapter(R.id.agenda_remote_list, i)
            views.setEmptyView(R.id.agenda_remote_list, R.id.empty)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.agenda_remote_list)
        }
    }
}

