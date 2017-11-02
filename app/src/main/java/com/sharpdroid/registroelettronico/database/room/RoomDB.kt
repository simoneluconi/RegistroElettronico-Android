package com.sharpdroid.registroelettronico.database.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.sharpdroid.registroelettronico.database.room.dao.LessonDao
import com.sharpdroid.registroelettronico.database.room.entities.Lesson
import java.util.*


@Database(entities = arrayOf(Lesson::class), version = 4)
@TypeConverters(Converters::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun lessonsDao(): LessonDao
}

internal object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return (date?.time)
    }
}
