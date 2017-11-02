package com.sharpdroid.registroelettronico.database.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sharpdroid.registroelettronico.database.room.dao.LessonDao
import com.sharpdroid.registroelettronico.database.room.entities.Lesson

@Database(entities = arrayOf(Lesson::class), version = 4)
abstract class RoomDB : RoomDatabase() {
    abstract fun lessonsDao(): LessonDao
}