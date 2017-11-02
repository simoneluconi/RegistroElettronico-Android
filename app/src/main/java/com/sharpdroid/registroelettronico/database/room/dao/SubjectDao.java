package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sharpdroid.registroelettronico.database.entities.Subject;
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo;

import java.util.List;

@Dao
public interface SubjectDao {

    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    Subject getSubject(long id);

    @Query("select * from SUBJECT where SUBJECT.ID IN (SELECT  SUBJECT_TEACHER.SUBJECT from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=:profile) ORDER BY DESCRIPTION ASC")
    LiveData<List<Subject>> getSubjects(long profile);

    @Update
    void updateSubject(Subject subject);

    @Update
    void updateSubjectInfo(SubjectInfo subject);

    @Query("DELETE FROM SUBJECT_TEACHER WHERE PROFILE = :profile")
    void delete(long profile);
}
