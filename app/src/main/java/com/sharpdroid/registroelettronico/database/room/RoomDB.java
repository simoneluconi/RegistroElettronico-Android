package com.sharpdroid.registroelettronico.database.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.sharpdroid.registroelettronico.database.room.dao.LessonDao;
import com.sharpdroid.registroelettronico.database.room.entities.LessonEntity;

@Database(entities = {LessonEntity.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class RoomDB extends RoomDatabase {
    public abstract LessonDao lessonsDao();
}
