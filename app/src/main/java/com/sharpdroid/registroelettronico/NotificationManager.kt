package com.sharpdroid.registroelettronico

import android.os.Looper
import android.util.Log
import android.util.SparseArray
import com.sharpdroid.registroelettronico.utils.EventType
import java.util.*


class NotificationManager {
    interface NotificationReceiver {
        fun didReceiveNotification(code: EventType, args: Array<in Any>)
    }

    private val observers: SparseArray<ArrayList<NotificationReceiver>> = SparseArray()

    fun addObserver(receiver: NotificationReceiver, vararg events: EventType) {
        checkLooper()
        events.forEach {
            val rec = observers[it.ordinal] ?: arrayListOf()
            rec.add(receiver)
            observers.put(it.ordinal, rec)
            if (BuildConfig.DEBUG)
                Log.d("NOTIFICATION", "REGISTER $receiver FOR EVENT ${it.name}")
        }
    }

    fun removeObserver(receiver: NotificationReceiver, vararg events: EventType) {
        checkLooper()
        events.forEach {
            observers[it.ordinal].remove(receiver)
            if (BuildConfig.DEBUG)
                Log.d("NOTIFICATION", "UNREGISTER $receiver FOR EVENT ${it.name}")
        }
    }

    fun postNotificationName(event: EventType, args: Array<in Any>?) {
        checkLooper()
        observers[event.ordinal]?.forEach { it.didReceiveNotification(event, args ?: emptyArray()) }
        if (BuildConfig.DEBUG)
            Log.d("NOTIFICATION", event.name)
    }

    private fun checkLooper() {
        if (BuildConfig.DEBUG) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw RuntimeException("postNotificationName allowed only from MAIN thread")
            }
        }
    }

    companion object {
        val instance = NotificationManager()
    }
}