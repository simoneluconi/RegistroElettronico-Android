package com.sharpdroid.registroelettronico.widget.agenda

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.pojos.LocalAgendaPOJO
import com.sharpdroid.registroelettronico.database.pojos.RemoteAgendaPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi

class AgendaService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return AgendaFactory(applicationContext, intent)
    }
}

private class AgendaFactory(val context: Context, val intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    val list = mutableListOf<Any>()
    val profile = Account.with(context).user

    override fun onCreate() {
        //should load data in onDataSetChanged()
    }

    override fun getViewAt(i: Int): RemoteViews {
        val current = list[i]

        // Header
        if (current is String) {
            val rv = RemoteViews(context.packageName, R.layout.adapter_header)
            rv.setTextViewText(R.id.content, current)
            return rv
        }

        // Local event
        if (current is LocalAgendaPOJO) {
            val rv = RemoteViews(context.packageName, R.layout.widget_agenda_event)
            rv.setTextViewText(R.id.title, current.event.title)
            rv.setTextViewText(R.id.subtitle, current.getSubjectOrAuthor())
            return rv
        }

        // Remote event
        if (current is RemoteAgendaPOJO) {
            val rv = RemoteViews(context.packageName, R.layout.widget_agenda_event)
            rv.setTextViewText(R.id.title, current.event.notes)
            rv.setTextViewText(R.id.subtitle, Metodi.capitalizeEach(current.event.subject
                    ?: current.event.author, true))
            return rv
        }

        throw IllegalStateException("Couldn't return any view...")
    }

    override fun getItemId(i: Int): Long {
        val current = list[i]
        return (current as? RemoteAgendaPOJO)?.event?.id
                ?: (current as? LocalAgendaPOJO)?.event?.id
                ?: -1L
    }

    override fun onDataSetChanged() {
        list.clear()
        val currentTime = System.currentTimeMillis()
        list.addAll(DatabaseHelper.database.eventsDao().getLocalSync(profile).filter { it.event.day - 3 * 24 * 3600000 >= currentTime })
        list.addAll(DatabaseHelper.database.eventsDao().getRemoteSync(profile).filter {
            it.event.start.time
            -3 * 24 * 3600000 >= currentTime
        })
    }

    override fun hasStableIds() = false
    override fun getCount() = list.size
    override fun getViewTypeCount() = 3
    override fun getLoadingView() = null
    override fun onDestroy() {
        list.clear()
    }

}