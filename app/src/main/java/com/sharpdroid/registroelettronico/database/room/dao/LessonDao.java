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

    @Query("SELECT * FROM LESSON WHERE M_SUBJECT_ID=:code GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC LIMIT 5")
    List<Lesson> loadLastLessons(long code);

    @Query("SELECT * FROM LESSON WHERE M_SUBJECT_ID=:code GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC LIMIT 5")
    LiveData<List<Lesson>> loadLessonsGrouped(long code);

    @Insert
    void insert(List<Lesson> lessons);

    @Query("DELETE FROM LESSON WHERE PROFILE = :profile")
    void delete(long profile);

}
