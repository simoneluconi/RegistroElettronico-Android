package com.sharpdroid.registroelettronico.database.room

import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
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
            database = Room.databaseBuilder(context, RoomDB::class.java, "registro-room.db")
                    .addMigrations(object : Migration(4, 5) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            database.execSQL("CREATE TABLE FILE_INFO")
                        }
                    })
                    .allowMainThreadQueries()
                    .build()
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isDatabaseCreated.value = true
                }
    }
}