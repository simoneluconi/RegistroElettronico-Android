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

                            //throw IllegalStateException("Not yet implemented")

                            with(database) {
                                execSQL("DROP TABLE ABSENCE")
                                execSQL("DROP TABLE COMMUNICATION")
                                execSQL("DROP TABLE FILE")
                                execSQL("DROP TABLE FOLDER")
                                execSQL("DROP TABLE GRADE")
                                execSQL("DROP TABLE LESSON")
                                execSQL("DROP TABLE NOTE")
                                execSQL("DROP TABLE PERIOD")
                                execSQL("DROP TABLE REMOTE_AGENDA")
                                execSQL("DROP TABLE SUBJECT")
                                execSQL("DROP TABLE SUBJECT_TEACHER")
                                execSQL("DROP TABLE TEACHER")

                                execSQL("CREATE TABLE `ABSENCE` (`ID` INTEGER NOT NULL, `TYPE` TEXT NOT NULL, `DATE` INTEGER NOT NULL, `JUSTIFIED` INTEGER NOT NULL, `REASON_CODE` TEXT, `REASON_DESC` TEXT, `PROFILE` INTEGER NOT NULL, `H_POS` INTEGER NOT NULL, `VALUE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `COMMUNICATION` (`ID` INTEGER NOT NULL, `DATE` INTEGER NOT NULL, `IS_READ` INTEGER NOT NULL, `EVT_CODE` TEXT NOT NULL, `MY_ID` INTEGER NOT NULL, `TITLE` TEXT NOT NULL, `CATEGORY` TEXT NOT NULL, `HAS_ATTACHMENT` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `FILE` (`ID` INTEGER NOT NULL, `CONTENT_NAME` TEXT NOT NULL, `OBJECT_ID` INTEGER NOT NULL, `TYPE` TEXT NOT NULL, `DATE` INTEGER NOT NULL, `FOLDER` INTEGER NOT NULL, `TEACHER` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `FOLDER` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `FOLDER_ID` INTEGER NOT NULL, `NAME` TEXT NOT NULL, `LAST_UPDATE` INTEGER NOT NULL, `TEACHER` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL)")
                                execSQL("CREATE TABLE `GRADE` (`M_CODE` TEXT NOT NULL, `M_COMPONENT_POS` INTEGER NOT NULL, `M_DATE` INTEGER NOT NULL, `M_DESCRIPTION` TEXT NOT NULL, `ID` INTEGER NOT NULL, `M_NOTES` TEXT NOT NULL, `M_PERIOD` INTEGER NOT NULL, `M_PERIOD_NAME` TEXT NOT NULL, `M_STRING_VALUE` TEXT NOT NULL, `M_SUBJECT_ID` INTEGER NOT NULL, `M_TYPE` TEXT NOT NULL, `M_UNDERLINED` INTEGER NOT NULL, `M_VALUE` REAL NOT NULL, `M_WEIGHT_FACTOR` REAL NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `LESSON` (`M_ARGUMENT` TEXT NOT NULL, `M_AUTHOR_NAME` TEXT NOT NULL, `M_CLASS_DESCRIPTION` TEXT NOT NULL, `M_CODE` TEXT NOT NULL, `M_DATE` INTEGER NOT NULL, `M_DURATION` INTEGER NOT NULL, `M_HOUR_POSITION` INTEGER NOT NULL, `ID` INTEGER NOT NULL, `M_SUBJECT_CODE` TEXT NOT NULL, `M_SUBJECT_DESCRIPTION` TEXT NOT NULL, `M_SUBJECT_ID` INTEGER NOT NULL, `M_TYPE` TEXT NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `NOTE` (`M_AUTHOR` TEXT NOT NULL, `M_DATE` INTEGER NOT NULL, `ID` INTEGER NOT NULL, `M_STATUS` INTEGER NOT NULL, `M_TEXT` TEXT NOT NULL, `M_WARNING` TEXT NOT NULL, `M_TYPE` TEXT NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `PERIOD` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `M_CODE` TEXT NOT NULL, `M_DESCRIPTION` TEXT NOT NULL, `M_END` INTEGER NOT NULL, `M_FINAL` INTEGER NOT NULL, `M_POSITION` INTEGER NOT NULL, `M_START` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL)")
                                execSQL("CREATE TABLE `REMOTE_AGENDA` (`ID` INTEGER NOT NULL, `START` INTEGER NOT NULL, `END` INTEGER NOT NULL, `IS_FULL_DAY` INTEGER NOT NULL, `NOTES` TEXT NOT NULL, `AUTHOR` TEXT NOT NULL, `PROFILE` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `SUBJECT` (`ID` INTEGER NOT NULL, `DESCRIPTION` TEXT NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("CREATE TABLE `SUBJECT_TEACHER` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `SUBJECT` INTEGER NOT NULL, `TEACHER` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL)")
                                execSQL("CREATE TABLE `TEACHER` (`ID` INTEGER NOT NULL, `TEACHER_NAME` TEXT NOT NULL, PRIMARY KEY(`ID`))")

                                //UPDATED ALL REMOTE TABLES

                                //NEED TO UPDATE OLD TABLES TO THE NEW ORM (communication_info, file_info, local_agenda, local_grade, profile, remote_agenda_info, subject_info)

                                execSQL("ALTER TABLE COMMUNICATION_INFO RENAME TO COMMUNICATION_INFO_OLD")
                                execSQL("CREATE TABLE `COMMUNICATION_INFO` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `TITLE` TEXT NOT NULL, `CONTENT` TEXT NOT NULL, `PATH` TEXT NOT NULL)")
                                execSQL("INSERT INTO COMMUNICATION_INFO(`ID`, `TITLE`, `CONTENT`, `PATH`) SELECT `ID`, `TITLE`, `CONTENT`, `PATH` FROM COMMUNICATION_INFO_OLD")
                                execSQL("DROP TABLE COMMUNICATION_INFO_OLD")

                                execSQL("ALTER TABLE FILE_INFO RENAME TO FILE_INFO_OLD")
                                execSQL("CREATE TABLE `FILE_INFO` (`ID` INTEGER NOT NULL, `PATH` TEXT NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("INSERT INTO FILE_INFO (ID, PATH) SELECT ID, PATH FROM FILE_INFO_OLD")
                                execSQL("DROP TABLE FILE_INFO_OLD")

                                execSQL("ALTER TABLE LOCAL_AGENDA RENAME TO LOCAL_AGENDA_OLD")
                                execSQL("UPDATE LOCAL_AGENDA_OLD SET COMPLETEDDATE=0 WHERE COMPLETEDDATE IS NULL")
                                execSQL("CREATE TABLE `LOCAL_AGENDA` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `TITLE` TEXT NOT NULL, `CONTENT` TEXT NOT NULL, `TYPE` TEXT NOT NULL, `DAY` INTEGER NOT NULL, `SUBJECT` INTEGER NOT NULL, `TEACHER` INTEGER NOT NULL, `COMPLETED_DATE` INTEGER, `PROFILE` INTEGER NOT NULL, `ARCHIVED` INTEGER NOT NULL)")
                                execSQL("INSERT INTO LOCAL_AGENDA(ID, TITLE, CONTENT, TYPE, DAY, SUBJECT, TEACHER, COMPLETED_DATE, PROFILE, ARCHIVED) SELECT ID, TITLE, CONTENT, TYPE, DAY, SUBJECT, TEACHER, COMPLETEDDATE as COMPLETED_DATE, PROFILE, ARCHIVED FROM LOCAL_AGENDA_OLD")
                                execSQL("DROP TABLE LOCAL_AGENDA_OLD")

                                execSQL("ALTER TABLE LOCAL_GRADE RENAME TO LOCAL_GRADE_OLD")
                                execSQL("CREATE TABLE `LOCAL_GRADE` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `VALUE` REAL NOT NULL, `VALUE_NAME` TEXT NOT NULL, `SUBJECT` INTEGER NOT NULL, `PERIOD` INTEGER NOT NULL, `TYPE` TEXT NOT NULL, `PROFILE` INTEGER NOT NULL)")
                                execSQL("INSERT INTO LOCAL_GRADE(ID, VALUE, VALUE_NAME, SUBJECT, PERIOD, TYPE, PROFILE) SELECT ID, VALUE, VALUENAME as VALUE_NAME, SUBJECT, PERIOD, TYPE, PROFILE FROM LOCAL_GRADE_OLD")
                                execSQL("DROP TABLE LOCAL_GRADE_OLD")

                                execSQL("ALTER TABLE PROFILE RENAME TO PROFILE_OLD")
                                execSQL("CREATE TABLE `PROFILE` (`USERNAME` TEXT NOT NULL, `NAME` TEXT NOT NULL, `PASSWORD` TEXT NOT NULL, `CLASSE` TEXT NOT NULL, `ID` INTEGER NOT NULL, `TOKEN` TEXT NOT NULL, `EXPIRE` INTEGER NOT NULL, `IDENT` TEXT NOT NULL, `IS_MULTI` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("INSERT INTO PROFILE(USERNAME, NAME, PASSWORD, CLASSE, ID, TOKEN, EXPIRE, IDENT, IS_MULTI) SELECT USERNAME, NAME, PASSWORD, CLASSE, ID, TOKEN, EXPIRE, IDENT, IS_MULTI FROM PROFILE_OLD")
                                execSQL("DROP TABLE PROFILE_OLD")

                                execSQL("ALTER TABLE REMOTE_AGENDA_INFO RENAME TO REMOTE_AGENDA_INFO_OLD")
                                execSQL("CREATE TABLE `REMOTE_AGENDA_INFO` (`ID` INTEGER NOT NULL, `COMPLETED` INTEGER NOT NULL, `ARCHIVED` INTEGER NOT NULL, `TEST` INTEGER NOT NULL, PRIMARY KEY(`ID`))")
                                execSQL("INSERT INTO REMOTE_AGENDA_INFO(ID, COMPLETED, ARCHIVED, TEST) SELECT ID, COMPLETED, ARCHIVED, TEST FROM REMOTE_AGENDA_INFO_OLD")
                                execSQL("DROP TABLE REMOTE_AGENDA_INFO_OLD")

                                execSQL("ALTER TABLE SUBJECT_INFO RENAME TO SUBJECT_INFO_OLD")
                                execSQL("CREATE TABLE `SUBJECT_INFO` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `TARGET` REAL NOT NULL, `DESCRIPTION` TEXT NOT NULL, `DETAILS` TEXT NOT NULL, `CLASSROOM` TEXT NOT NULL, `SUBJECT` INTEGER NOT NULL, `PROFILE` INTEGER NOT NULL)")
                                execSQL("INSERT INTO SUBJECT_INFO(ID, TARGET, DESCRIPTION, DETAILS, CLASSROOM, SUBJECT, PROFILE) SELECT ID, TARGET, DESCRIPTION, DETAILS, CLASSROOM, SUBJECT, PROFILE FROM SUBJECT_INFO_OLD")
                                execSQL("DROP TABLE SUBJECT_INFO_OLD")
                            }
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