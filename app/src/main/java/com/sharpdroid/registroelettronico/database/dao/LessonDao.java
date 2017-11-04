package com.sharpdroid.registroelettronico.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.support.annotation.NonNull;

import com.sharpdroid.registroelettronico.database.entities.Lesson;
import com.sharpdroid.registroelettronico.database.pojos.LessonMini;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface LessonDao {
    @Query("SELECT ID, M_ARGUMENT, M_AUTHOR_NAME, M_DATE, M_HOUR_POSITION, M_SUBJECT_DESCRIPTION, COUNT(ID) as `M_DURATION`, M_CLASS_DESCRIPTION, M_CODE, M_SUBJECT_CODE, M_SUBJECT_ID, M_TYPE, PROFILE FROM LESSON WHERE M_DATE = :date AND PROFILE=:profile GROUP BY M_ARGUMENT, M_AUTHOR_NAME ORDER BY M_HOUR_POSITION ASC")
    @NonNull
    LiveData<List<Lesson>> loadLessons(long profile, long date);

    @Query("SELECT * FROM LESSON WHERE M_SUBJECT_ID=:code GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC LIMIT 5")
    Flowable<List<Lesson>> loadLastLessons(long code);


    @Query("SELECT * FROM LESSON WHERE M_SUBJECT_ID=:code GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC")
    Flowable<List<LessonMini>> loadLessons(long code);

    @Transaction
    @Query("SELECT * FROM LESSON WHERE PROFILE=:profile AND (M_ARGUMENT LIKE :query OR M_SUBJECT_DESCRIPTION LIKE :query)")
    Flowable<List<Lesson>> query(String query, long profile);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Lesson> lessons);

    @Query("DELETE FROM LESSON WHERE PROFILE = :profile")
    void delete(long profile);

}
