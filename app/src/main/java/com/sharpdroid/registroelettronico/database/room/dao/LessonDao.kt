package com.sharpdroid.registroelettronico.database.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.sharpdroid.registroelettronico.database.room.entities.Lesson

@Dao
interface LessonDao {
    @Query("SELECT * FROM LESSON WHERE PROFILE = :profile")
    fun loadProfile(profile: Long): LiveData<List<Lesson>>
}