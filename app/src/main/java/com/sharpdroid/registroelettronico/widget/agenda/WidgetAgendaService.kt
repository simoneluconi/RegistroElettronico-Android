package com.sharpdroid.registroelettronico.widget.agenda

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetAgendaService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetAgendaFactory(applicationContext, intent)
    }
}