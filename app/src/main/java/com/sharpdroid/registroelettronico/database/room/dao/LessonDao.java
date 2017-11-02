package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.sharpdroid.registroelettronico.database.entities.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    @Query("SELECT * FROM LESSON WHERE PROFILE = :profile")
    @NonNull
    LiveData<List<Lesson>> loadLessons(Long profile);

    @Insert
    void insertLessons(List<Lesson> lessons);

    @Query("DELETE FROM LESSON WHERE PROFILE = :profile")
    void deleteLessons(Long profile);

}
