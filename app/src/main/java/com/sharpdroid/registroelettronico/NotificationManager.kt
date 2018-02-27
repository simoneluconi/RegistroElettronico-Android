package com.sharpdroid.registroelettronico

import android.os.Looper
import android.util.SparseArray
import java.util.*

class NotificationManager {
    interface NotificationReceiver {
        fun didReceiveNotification(code: Int, args: Array<in Any>)
    }

    private val observers: SparseArray<ArrayList<NotificationReceiver>> = SparseArray()

    fun addObserver(receiver: NotificationReceiver, vararg events: Int) {
        checkLooper()
        events.forEach {
            val rec = observers[it] ?: arrayListOf()
            rec.add(receiver)
            observers.put(it, rec)
        }
    }

    fun removeObserver(receiver: NotificationReceiver, vararg events: Int) {
        checkLooper()
        events.forEach {
            observers[it].remove(receiver)
        }
    }

    fun postNotificationName(event: Int, args: Array<in Any>?) {
        checkLooper()
        observers[event]?.forEach { it.didReceiveNotification(event, args ?: emptyArray()) }
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