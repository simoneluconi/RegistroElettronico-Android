package com.sharpdroid.registroelettronico.database.room

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?) = if (value == null) null else Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time

    @TypeConverter
    fun fromInteger(value: Int?) = value == 1

    @TypeConverter
    fun booleanToInteger(bool: Boolean?) = if (bool == true) 1 else 0
}