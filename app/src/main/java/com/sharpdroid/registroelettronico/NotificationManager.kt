package com.sharpdroid.registroelettronico

import android.os.Looper
import android.util.Log
import android.util.SparseArray
import java.util.*


class NotificationManager {
    interface NotificationReceiver {
        fun didReceiveNotification(code: Int, vararg args: Array<out Any>)
    }

    private val observers: SparseArray<ArrayList<Any>> = SparseArray()

    fun addObserver(receiver: NotificationReceiver, vararg events: Int) {
        checkLooper()
        events.forEach {
            val rec = observers[it] ?: arrayListOf()
            rec.add(receiver)
            observers.put(it, rec)
            Log.d("NOTIFICATION", "REGISTER $receiver FOR EVENT $it")
        }
    }

    fun removeObserver(receiver: NotificationReceiver, vararg events: Int) {
        checkLooper()
        events.forEach {
            observers[it].remove(receiver)
            Log.d("NOTIFICATION", "UNREGISTER $receiver FOR EVENT $it")
        }
    }

    fun postNotificationName(event: Int, vararg args: Any) {
        checkLooper()
        observers[event]?.forEach { i -> (i as NotificationReceiver).didReceiveNotification(event, args) }
        Log.d("NOTIFICATION", "FIRE")
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