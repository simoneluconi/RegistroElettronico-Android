package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.database.entities.SubjectTeacher
import com.sharpdroid.registroelettronico.database.entities.Teacher
import com.sharpdroid.registroelettronico.database.pojos.AverageType
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.pojos.SubjectWithLessons
import io.reactivex.Flowable

@Dao
interface SubjectDao {

    @Transaction
    @Query("select * from SUBJECT where ID in (SELECT SUBJECT_TEACHER.SUBJECT FROM SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=:profile)")
    fun getSubjectWithLessons(profile: Long): LiveData<List<SubjectWithLessons>>

    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    fun getSubject(id: Long): Subject?

    @Transaction
    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    fun getSubjectInfo(id: Long): LiveData<SubjectPOJO>

    @Transaction
    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    fun getSubjectInfoFlowable(id: Long): Flowable<List<SubjectPOJO>>

    @Transaction
    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    fun getSubjectInfoBlocking(id: Long): SubjectPOJO?

    @Query("select * from SUBJECT where SUBJECT.ID IN (SELECT  SUBJECT_TEACHER.SUBJECT from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=:profile) ORDER BY DESCRIPTION ASC")
    fun getSubjects(profile: Long): LiveData<List<Subject>>

    @Transaction
    @Query("select * from SUBJECT where SUBJECT.ID IN (SELECT  SUBJECT_TEACHER.SUBJECT from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=:profile) ORDER BY DESCRIPTION ASC")
    fun getSubjectsWithInfoBlocking(profile: Long): List<SubjectPOJO>

    @Query("SELECT SUM(M_VALUE) as `SUM` , 'Generale' as `M_TYPE`, COUNT(M_VALUE) as `COUNT`  FROM GRADE WHERE M_VALUE!=0 AND M_SUBJECT_ID=:subject AND PROFILE=:profile LIMIT 1")
    fun getAverage(subject: Long, profile: Long): AverageType

    @Query("select * from TEACHER where TEACHER.ID IN (SELECT  SUBJECT_TEACHER.TEACHER from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.SUBJECT=:subjectId AND SUBJECT_TEACHER.PROFILE=:profile) ORDER BY TEACHER_NAME ASC")
    fun getTeachersOfSubject(subjectId: Long, profile: Long): List<Teacher>

    @Query("select * from TEACHER where TEACHER.ID IN (SELECT  SUBJECT_TEACHER.TEACHER from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=:profile) ORDER BY TEACHER_NAME ASC")
    fun getTeachersOfProfile(profile: Long): List<Teacher>

    @Query("DELETE FROM SUBJECT WHERE ID IN (SELECT SUBJECT FROM SUBJECT_TEACHER WHERE PROFILE=:profile)")
    fun deleteSubjects(profile: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSubject(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: List<Subject>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg info: SubjectInfo): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg info: SubjectTeacher)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(teachers: Collection<Teacher>)

    @Update
    fun updateSubjectInfo(subject: SubjectInfo): Int

    @Query("DELETE FROM SUBJECT_TEACHER WHERE PROFILE = :profile")
    fun delete(profile: Long)

    @Query("DELETE FROM SUBJECT_TEACHER WHERE PROFILE=:profile AND SUBJECT=:subject AND TEACHER=:teacher")
    fun deleteSingle(profile: Long, subject: Long, teacher: Long)

    @Query("UPDATE SUBJECT_INFO SET TARGET = 0 WHERE PROFILE = :profile")
    fun removeTargets(profile: Long)
}