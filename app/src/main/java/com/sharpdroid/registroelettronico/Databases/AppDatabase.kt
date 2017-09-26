package com.sharpdroid.registroelettronico.Databases

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.sharpdroid.registroelettronico.Databases.DAOs.ProfileDAO
import java.util.*


@Database(entities = arrayOf(), version = 1, exportSchema = false)
@TypeConverters(AppDatabase.Converters::class)

abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDAO

    object Converters {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? {
            return Date(value ?: return null)
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}