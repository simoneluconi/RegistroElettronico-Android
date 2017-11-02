package com.sharpdroid.registroelettronico.database.room

import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.Room
import android.content.Context
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicBoolean

object DatabaseHelper {

    val isDatabaseCreated = MutableLiveData<Boolean>()

    lateinit var database: RoomDB

    val isInitializing = AtomicBoolean(true)

    fun createDb(context: Context) {
        if (isInitializing.compareAndSet(true, false).not()) {
            return
        }

        isDatabaseCreated.value = false

        Completable.fromAction({
            database = Room.databaseBuilder(context, RoomDB::class.java, "registro-room.db").build()
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isDatabaseCreated.value = true
                }
    }
}