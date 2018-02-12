package com.sharpdroid.registroelettronico.widget.orario

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetOrarioService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return WidgetOrarioFactory(applicationContext, intent)
    }
}