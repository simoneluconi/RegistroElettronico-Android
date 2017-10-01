package com.sharpdroid.registroelettronico

import android.os.Looper
import android.util.Log
import android.util.SparseArray
import java.util.*


class NotificationManager {
    interface NotificationReceiver {
        fun didReceiveNotification(code: Int)
    }

    private val observers: SparseArray<ArrayList<Any>> = SparseArray()

    fun addObserver(receiver: NotificationReceiver, vararg events: Int) {
        if (BuildConfig.DEBUG) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw RuntimeException("postNotificationName allowed only from MAIN thread")
            }
        }
        events.forEach {
            val rec = observers[it] ?: arrayListOf()
            rec.add(receiver)
            observers.put(it, rec)
            Log.d("NOTIFICATION", "REGISTER $receiver FOR EVENT $it")
        }
    }

    fun removeObserver(receiver: NotificationReceiver, vararg events: Int) {
        if (BuildConfig.DEBUG) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw RuntimeException("postNotificationName allowed only from MAIN thread")
            }
        }
        events.forEach {
            observers[it].remove(receiver)
            Log.d("NOTIFICATION", "UNREGISTER $receiver FOR EVENT $it")
        }
    }

    fun postNotificationName(vararg events: Int) {
        //TODO: collect events to send together
        if (BuildConfig.DEBUG) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw RuntimeException("postNotificationName allowed only from MAIN thread")
            }
        }
        events.forEach { observers[it]?.forEach { i -> (i as NotificationReceiver).didReceiveNotification(it); Log.d("NOTIFICATION", "FIRE") } }
    }

    companion object {
        const val UPDATE_AGENDA = 1
        const val UPDATE_MARKS = 2
        const val UPDATE_LESSONS = 3
        const val UPDATE_SUBJECTS = 4
        const val UPDATE_FOLDERS = 5
        const val UPDATE_ABSENCES = 6
        const val UPDATE_BACHECA = 7
        const val UPDATE_NOTES = 8
        const val UPDATE_PERIODS = 9

        val istance = NotificationManager()
    }
}