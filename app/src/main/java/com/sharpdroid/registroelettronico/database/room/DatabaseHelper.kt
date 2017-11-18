@file:Suppress("UNREACHABLE_CODE")

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

    private val isDatabaseCreated = MutableLiveData<Boolean>()

    lateinit var database: RoomDB

    private val isInitializing = AtomicBoolean(true)

    fun createDb(context: Context) {
        if (isInitializing.compareAndSet(true, false).not()) {
            return
        }

        isDatabaseCreated.value = false

        Completable.fromAction({
            database = Room.databaseBuilder(context, RoomDB::class.java, "registro.db")
                    .allowMainThreadQueries()
                    .addMigrations(object : Migration(4, 5) {
                        override fun migrate(database: SupportSQLiteDatabase) {

                            throw IllegalStateException("Not yet implemented")

                            database.execSQL("DROP TABLE ABSENCE")
                            database.execSQL("DROP TABLE COMMUNICATION")
                            database.execSQL("DROP TABLE FILE")
                            database.execSQL("DROP TABLE FOLDER")
                            database.execSQL("DROP TABLE GRADE")
                            database.execSQL("DROP TABLE LESSON")
                            database.execSQL("DROP TABLE NOTE")
                            database.execSQL("DROP TABLE PERIOD")
                            database.execSQL("DROP TABLE REMOTE_AGENDA")
                            database.execSQL("DROP TABLE SUBJECT")
                            database.execSQL("DROP TABLE SUBJECT_TEACHER")
                            database.execSQL("DROP TABLE TEACHER")

                            database.execSQL("CREATE TABLE `ABSENCE` (`ID` INTEGER NOT NULL, `TYPE` TEXT NOT NULL, `DATE` INTEGER NOT NULL, `JUSTIFIED` INTEGER NOT NULL, `REASON_CODE` TEXT, `REASON_DESC` TEXT, `PROFILE` INTEGER NOT NULL, `H_POS` INTEGER NOT NULL, `VALUE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `COMMUNICATION` (`ID` INTEGER NOT NULL, `DATE` INTEGER NOT NULL, `IS_READ` INTEGER NOT NULL, `EVT_CODE` TEXT NOT NULL, `MY_ID` INTEGER NOT NULL, `TITLE` TEXT NOT NULL, `CATEGORY` TEXT NOT NULL, `HAS_ATTACHMENT` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `FILE` (`ID` INTEGER NOT NULL, `CONTENT_NAME` TEXT NOT NULL, `OBJECT_ID` INTEGER NOT NULL, `TYPE` TEXT NOT NULL, `DATE` INTEGER NOT NULL, `FOLDER` INTEGER NOT NULL, `TEACHER` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `FOLDER` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `FOLDER_ID` INTEGER NOT NULL, `NAME` TEXT NOT NULL, `LAST_UPDATE` INTEGER NOT NULL, `TEACHER` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL)")
                            database.execSQL("CREATE TABLE `GRADE` (`M_CODE` TEXT NOT NULL, `M_COMPONENT_POS` INTEGER NOT NULL, `M_DATE` INTEGER NOT NULL, `M_DESCRIPTION` TEXT NOT NULL, `ID` INTEGER NOT NULL, `M_NOTES` TEXT NOT NULL, `M_PERIOD` INTEGER NOT NULL, `M_PERIOD_NAME` TEXT NOT NULL, `M_STRING_VALUE` TEXT NOT NULL, `M_SUBJECT_ID` INTEGER NOT NULL, `M_TYPE` TEXT NOT NULL, `M_UNDERLINED` INTEGER NOT NULL, `M_VALUE` REAL NOT NULL, `M_WEIGHT_FACTOR` REAL NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `LESSON` (`M_ARGUMENT` TEXT NOT NULL, `M_AUTHOR_NAME` TEXT NOT NULL, `M_CLASS_DESCRIPTION` TEXT NOT NULL, `M_CODE` TEXT NOT NULL, `M_DATE` INTEGER NOT NULL, `M_DURATION` INTEGER NOT NULL, `M_HOUR_POSITION` INTEGER NOT NULL, `ID` INTEGER NOT NULL, `M_SUBJECT_CODE` TEXT NOT NULL, `M_SUBJECT_DESCRIPTION` TEXT NOT NULL, `M_SUBJECT_ID` INTEGER NOT NULL, `M_TYPE` TEXT NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `NOTE` (`M_AUTHOR` TEXT NOT NULL, `M_DATE` INTEGER NOT NULL, `ID` INTEGER NOT NULL, `M_STATUS` INTEGER NOT NULL, `M_TEXT` TEXT NOT NULL, `M_WARNING` TEXT NOT NULL, `M_TYPE` TEXT NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `PERIOD` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `M_CODE` TEXT NOT NULL, `M_DESCRIPTION` TEXT NOT NULL, `M_END` INTEGER NOT NULL, `M_FINAL` INTEGER NOT NULL, `M_POSITION` INTEGER NOT NULL, `M_START` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL)")
                            database.execSQL("CREATE TABLE `REMOTE_AGENDA` (`ID` INTEGER NOT NULL, `START` INTEGER NOT NULL, `END` INTEGER NOT NULL, `IS_FULL_DAY` INTEGER NOT NULL, `NOTES` TEXT NOT NULL, `AUTHOR` TEXT NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `SUBJECT` (`ID` INTEGER NOT NULL, `DESCRIPTION` TEXT NOT NULL, PRIMARY KEY(`ID`))")
                            database.execSQL("CREATE TABLE `SUBJECT_TEACHER` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `SUBJECT` INTEGER NOT NULL, `TEACHER` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL)")
                            database.execSQL("CREATE TABLE `TEACHER` (`ID` INTEGER NOT NULL, `TEACHER_NAME` TEXT NOT NULL, PRIMARY KEY(`ID`))")

                            //UPDATED ALL REMOTE TABLES

                            //NEED TO UPDATE OLD TABLES TO THE NEW ORM (communication_info, file_info, local_agenda, local_grade, profile, remote_agenda_info, subject_info)


                        }
                    })
                    .build()
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isDatabaseCreated.value = true
                }
    }
}