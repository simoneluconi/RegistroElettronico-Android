package com.sharpdroid.registroelettronico.database.room;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Boolean fromInteger(Integer value) {
        return value == 1;
    }

    @TypeConverter
    public static Integer booleanToInteger(Boolean bool) {
        return bool ? 1 : 0;
    }
}