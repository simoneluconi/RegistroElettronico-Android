package com.sharpdroid.registroelettronico.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sharpdroid.registroelettronico.database.entities.Communication
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.Note
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        val ACTION_NOTIFICATION_DISMISSED = "com.sharpdroid.registroelettronico.NOTIFICATION_DISMISSED"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        if (ACTION_NOTIFICATION_DISMISSED == action) {
            dismissNotification(intent)
        }
    }

    private fun dismissNotification(intent: Intent) {
        val content = intent.getSerializableExtra("list") as List<Any>
        val firstElement = content.first()
        when (firstElement) {
            is RemoteAgenda -> DatabaseHelper.database.eventsDao().insert(content as List<RemoteAgenda>)
            is Grade -> DatabaseHelper.database.gradesDao().insert(content as List<Grade>)
            is Communication -> DatabaseHelper.database.communicationsDao().insert(content as List<Communication>)
            is Note -> DatabaseHelper.database.notesDao().insert(content as List<Note>)
        }
        Log.d("NotificationReceiver", "Dismissed ${content.size} items")
    }
}