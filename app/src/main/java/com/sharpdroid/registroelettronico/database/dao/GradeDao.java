package com.sharpdroid.registroelettronico.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.sharpdroid.registroelettronico.database.entities.Grade;
import com.sharpdroid.registroelettronico.database.entities.LocalGrade;

import java.util.List;

@Dao
public interface GradeDao {

    @Query("SELECT * FROM GRADE WHERE PROFILE = :profile")
    LiveData<List<Grade>> getGrades(long profile);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Grade> grades);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertGrade(LocalGrade grade);

    @Delete
    void deleteGrade(LocalGrade grade);

    @Query("DELETE FROM GRADE WHERE PROFILE = :profile")
    void delete(long profile);
}
