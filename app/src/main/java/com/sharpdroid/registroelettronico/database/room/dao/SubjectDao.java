package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sharpdroid.registroelettronico.database.entities.Subject;
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo;
import com.sharpdroid.registroelettronico.database.entities.SubjectTeacher;
import com.sharpdroid.registroelettronico.database.entities.Teacher;

import java.util.List;

@Dao
public interface SubjectDao {

    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    Subject getSubject(long id);

    @Query("select * from SUBJECT where SUBJECT.ID IN (SELECT  SUBJECT_TEACHER.SUBJECT from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=:profile) ORDER BY DESCRIPTION ASC")
    LiveData<List<Subject>> getSubjects(long profile);

    @Query("DELETE FROM SUBJECT WHERE ID IN (SELECT SUBJECT FROM SUBJECT_TEACHER WHERE PROFILE=:profile)")
    void deleteSubjects(long profile);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSubject(Subject subject);

    @Insert
    void insert(List<Subject> info);

    @Insert
    void insert(SubjectInfo... info);

    @Insert
    void insert(SubjectTeacher... info);

    @Insert
    void insert(Teacher[] info);

    @Update
    void updateSubjectInfo(SubjectInfo subject);

    @Query("DELETE FROM SUBJECT_TEACHER WHERE PROFILE = :profile")
    void delete(long profile);

    @Query("DELETE FROM SUBJECT_TEACHER WHERE PROFILE=:profile AND SUBJECT=:subject AND TEACHER=:teacher")
    void deleteSingle(long profile, long subject, long teacher);

    @Query("select * from TEACHER where TEACHER.ID IN (select TEACHER FROM FOLDER WHERE FOLDER.PROFILE=:profile)")
    List<Teacher> getTeachers(long profile);

}
