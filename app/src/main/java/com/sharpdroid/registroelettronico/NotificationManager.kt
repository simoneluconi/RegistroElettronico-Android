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
            Log.d("NOTIFICATION", "REGISTER $receiver FOR EVENT ${Companion.events[it]}")
        }
    }

    fun removeObserver(receiver: NotificationReceiver, vararg events: Int) {
        checkLooper()
        events.forEach {
            observers[it].remove(receiver)
            Log.d("NOTIFICATION", "UNREGISTER $receiver FOR EVENT ${Companion.events[it]}")
        }
    }

    fun postNotificationName(event: Int, vararg args: Any) {
        checkLooper()
        observers[event]?.forEach { i -> (i as NotificationReceiver).didReceiveNotification(event, args) }
        Log.d("NOTIFICATION", events[event])
    }

    private fun checkLooper() {
        if (BuildConfig.DEBUG) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw RuntimeException("postNotificationName allowed only from MAIN thread")
            }
        }
    }

    companion object {
        private val events = arrayOf("UPDATE_AGENDA_OK",
                "UPDATE_AGENDA_START",
                "UPDATE_AGENDA_KO",
                "UPDATE_MARKS_OK",
                "UPDATE_MARKS_START",
                "UPDATE_MARKS_KO",
                "UPDATE_LESSONS_OK",
                "UPDATE_LESSONS_START",
                "UPDATE_LESSONS_KO",
                "UPDATE_SUBJECTS_OK",
                "UPDATE_SUBJECTS_START",
                "UPDATE_SUBJECTS_KO",
                "UPDATE_FOLDERS_OK",
                "UPDATE_FOLDERS_START",
                "UPDATE_FOLDERS_KO",
                "UPDATE_ABSENCES_OK",
                "UPDATE_ABSENCES_START",
                "UPDATE_ABSENCES_KO",
                "UPDATE_BACHECA_OK",
                "UPDATE_BACHECA_START",
                "UPDATE_BACHECA_KO",
                "UPDATE_NOTES_OK",
                "UPDATE_NOTES_START",
                "UPDATE_NOTES_KO",
                "UPDATE_PERIODS_OK",
                "UPDATE_PERIODS_START",
                "UPDATE_PERIODS_KO",
                "UPDATE_TEACHERS_OK",
                "UPDATE_TEACHERS_START",
                "UPDATE_TEACHERS_KO")

        val instance = NotificationManager()
    }
}