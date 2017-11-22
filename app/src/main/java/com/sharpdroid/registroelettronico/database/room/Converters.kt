package com.sharpdroid.registroelettronico.database.room

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = if (value == null) null else Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromInteger(value: Int?): Boolean = value == 1

    @TypeConverter
    fun booleanToInteger(bool: Boolean?): Int? = if (bool == true) 1 else 0
}