package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.database.entities.SubjectTeacher
import com.sharpdroid.registroelettronico.database.entities.Teacher
import com.sharpdroid.registroelettronico.database.pojos.AverageType
import com.sharpdroid.registroelettronico.database.pojos.LessonMini
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.pojos.SubjectWithLessons
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class SubjectDao {

    //filter all SubjectInfos by Profile
    open fun getSubjectsAndLessons(profile: Long): Flowable<List<SubjectWithLessons>> {
        return DatabaseHelper.database.lessonsDao().flowableLessons(profile).map { allLessons ->
            getSubjects(profile).map { subject -> SubjectWithLessons(subject, listOfNotNull(getSubjectInfo(subject.id, profile)), allLessons.filter { it.mSubjectId == subject.id.toInt() }.map { LessonMini(it.mArgument, it.mAuthorName, it.mDate, it.mSubjectDescription) }) }
        }
    }

    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    abstract fun getSubject(id: Long): Subject?

    @Query("SELECT * FROM SUBJECT_INFO WHERE SUBJECT=:subject AND PROFILE=:profile LIMIT 1")
    abstract fun getSubjectInfo(subject: Long, profile: Long): SubjectInfo?


    @Transaction
    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    abstract fun _getSubjectPOJO(id: Long): LiveData<SubjectPOJO>

    //filter all SubjectInfos by Profile
    open fun getSubjectPOJO(id: Long, profile: Long): LiveData<SubjectPOJO> {
        return Transformations.map(_getSubjectPOJO(id), { input: SubjectPOJO? ->
            input?.apply {
                input.subjectInfo = input.subjectInfo.filter { it.profile == profile }
            }
            input
        })
    }

    @Transaction
    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    abstract fun _getSubjectPOJOFlowable(id: Long): Flowable<List<SubjectPOJO>>

    //filter all SubjectInfos by Profile
    open fun getSubjectPOJOFlowable(id: Long, profile: Long): Flowable<List<SubjectPOJO>> {
        return _getSubjectPOJOFlowable(id).map {
            it.map { subjectPOJO ->
                subjectPOJO.subjectInfo = subjectPOJO.subjectInfo.filter { it.profile == profile }
                subjectPOJO
            }
        }
    }

    @Transaction
    @Query("SELECT * FROM SUBJECT WHERE ID = :id LIMIT 1")
    abstract fun _getSubjectPOJOBlocking(id: Long): SubjectPOJO?

    //filter all SubjectInfos by Profile
    open fun getSubjectPOJOBlocking(id: Long, profile: Long): SubjectPOJO? {
        val x = _getSubjectPOJOBlocking(id)
        x?.let {
            it.subjectInfo = it.subjectInfo.filter { it.profile == profile }
        }
        return x
    }

    @Transaction
    @Query("select SUBJECT.* from SUBJECT left join SUBJECT_TEACHER ON SUBJECT.ID = SUBJECT_TEACHER.SUBJECT where SUBJECT_TEACHER.PROFILE=:profile group by SUBJECT.ID order by DESCRIPTION asc")
    abstract fun _getAllSubjectsPOJOBlocking(profile: Long): List<SubjectPOJO>

    //filter all SubjectInfos by Profile
    open fun getAllSubjectsPOJOBlocking(profile: Long): List<SubjectPOJO> {
        return _getAllSubjectsPOJOBlocking(profile).map {
            it.subjectInfo = it.subjectInfo.filter { it.profile == profile }
            it
        }
    }


    @Query("select SUBJECT.* from SUBJECT left join SUBJECT_TEACHER ON SUBJECT.ID = SUBJECT_TEACHER.SUBJECT where SUBJECT_TEACHER.PROFILE=:profile order by DESCRIPTION asc")
    abstract fun getSubjects(profile: Long): List<Subject>

    @Query("SELECT SUM(M_VALUE) as `SUM` , 'Generale' as `M_TYPE`, COUNT(M_VALUE) as `COUNT`  FROM GRADE WHERE M_VALUE!=0 AND M_SUBJECT_ID=:subject AND PROFILE=:profile AND ID NOT IN (SELECT ID FROM EXCLUDED_MARKS) LIMIT 1")
    abstract fun getAverage(subject: Long, profile: Long): LiveData<AverageType>


    @Query("select TEACHER.* from TEACHER left join SUBJECT_TEACHER ON TEACHER.ID = SUBJECT_TEACHER.TEACHER where SUBJECT_TEACHER.PROFILE=:profile order by TEACHER_NAME asc")
    abstract fun getTeachers(profile: Long): Single<List<Teacher>>

    @Query("select * from TEACHER where TEACHER.ID IN (SELECT  SUBJECT_TEACHER.TEACHER from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.SUBJECT=:subjectId AND SUBJECT_TEACHER.PROFILE=:profile) ORDER BY TEACHER_NAME ASC")
    abstract fun getTeachersOfSubject(subjectId: Long, profile: Long): List<Teacher>

    @Query("select * from TEACHER where TEACHER.ID IN (SELECT  SUBJECT_TEACHER.TEACHER from SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=:profile) ORDER BY TEACHER_NAME ASC")
    abstract fun getTeachersOfProfile(profile: Long): List<Teacher>


    @Query("DELETE FROM SUBJECT WHERE ID IN (SELECT SUBJECT FROM SUBJECT_TEACHER WHERE PROFILE=:profile)")
    abstract fun deleteSubjects(profile: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateSubject(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(info: List<Subject>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg info: SubjectInfo): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg info: SubjectTeacher)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(teachers: Collection<Teacher>)

    @Update
    abstract fun updateSubjectInfo(subject: SubjectInfo): Int

    @Query("DELETE FROM SUBJECT_TEACHER WHERE PROFILE = :profile")
    abstract fun delete(profile: Long)

    @Query("DELETE FROM SUBJECT_TEACHER WHERE PROFILE=:profile AND SUBJECT=:subject AND TEACHER=:teacher")
    abstract fun deleteSingle(profile: Long, subject: Long, teacher: Long)

    @Query("UPDATE SUBJECT_INFO SET TARGET = 0 WHERE PROFILE = :profile")
    abstract fun removeTargets(profile: Long)
}