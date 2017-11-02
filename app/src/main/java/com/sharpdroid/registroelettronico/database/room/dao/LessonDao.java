package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.sharpdroid.registroelettronico.database.room.entities.LessonEntity;

import java.util.List;

@Dao
public interface LessonDao {
    @Query("SELECT * FROM LESSON WHERE PROFILE = :profile")
    @NonNull
    LiveData<List<LessonEntity>> loadProfile(Long profile);
}
