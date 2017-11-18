package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Average
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.LocalGrade
import com.sharpdroid.registroelettronico.database.pojos.AverageType
import io.reactivex.Flowable

@Dao
interface GradeDao {

    @Query("SELECT * FROM GRADE WHERE PROFILE = :profile")
    fun getGrades(profile: Long): LiveData<List<Grade>>

    @Query("SELECT * FROM GRADE WHERE PROFILE = :profile")
    fun getGradesList(profile: Long): List<Grade>

    @Query("SELECT SUM(GRADE.M_VALUE) as `SUM`, COUNT(CASE WHEN GRADE.M_VALUE>0 THEN 1 END) as `COUNT`, coalesce(nullif(SUBJECT_INFO.DESCRIPTION,''), GRADE.M_DESCRIPTION) as NAME, GRADE.M_SUBJECT_ID, coalesce(SUBJECT_INFO.TARGET, 0) as TARGET \n" +
            "FROM GRADE \n" +
            "LEFT JOIN SUBJECT_INFO ON SUBJECT_INFO.SUBJECT=GRADE.M_SUBJECT_ID\n" +
            "WHERE GRADE.PROFILE=:profile AND GRADE.M_PERIOD=:period \n" +
            "GROUP BY GRADE.M_SUBJECT_ID")
    fun getAverages(profile: Long, period: Long): LiveData<List<Average>>

    @Query("SELECT SUM(GRADE.M_VALUE) as `SUM`, COUNT(CASE WHEN GRADE.M_VALUE>0 THEN 1 END) as `COUNT`, coalesce(nullif(SUBJECT_INFO.DESCRIPTION,''), GRADE.M_DESCRIPTION) as NAME, GRADE.M_SUBJECT_ID, coalesce(SUBJECT_INFO.TARGET, 0) as TARGET \n" +
            "FROM GRADE \n" +
            "LEFT JOIN SUBJECT_INFO ON SUBJECT_INFO.SUBJECT=GRADE.M_SUBJECT_ID\n" +
            "WHERE GRADE.PROFILE=:profile \n" +
            "GROUP BY GRADE.M_SUBJECT_ID")
    fun getAllAverages(profile: Long): LiveData<List<Average>>

    @Query("SELECT SUM(M_VALUE) as `SUM`, M_TYPE, COUNT(CASE WHEN M_VALUE!=0 THEN 1 END) AS `COUNT` FROM GRADE WHERE PROFILE=:profile AND M_SUBJECT_ID=:subject AND M_PERIOD=:period GROUP BY M_TYPE")
    fun getPeriodTypeAverage(profile: Long, subject: Long, period: Int): LiveData<List<AverageType>>

    @Query("SELECT SUM(M_VALUE) as `SUM`, M_TYPE, COUNT(CASE WHEN M_VALUE!=0 THEN 1 END) AS `COUNT` FROM GRADE WHERE PROFILE=:profile AND M_SUBJECT_ID=:subject GROUP BY M_TYPE")
    fun getAllTypeAverage(profile: Long, subject: Long): LiveData<List<AverageType>>

    @Query("SELECT * FROM GRADE WHERE PROFILE=:profile AND M_SUBJECT_ID=:subject AND M_PERIOD=:period")
    fun periodGrades(profile: Long, subject: Long, period: Int): LiveData<List<Grade>>

    @Query("SELECT * FROM GRADE WHERE PROFILE=:profile AND M_SUBJECT_ID=:subject")
    fun allPeriodsGrades(profile: Long, subject: Long): LiveData<List<Grade>>

    @Query("SELECT * FROM LOCAL_GRADE WHERE PROFILE=:profile AND SUBJECT=:subject AND PERIOD=:period")
    fun periodLocalGrades(profile: Long, subject: Long, period: Int): Flowable<List<LocalGrade>>

    @Query("SELECT * FROM LOCAL_GRADE WHERE PROFILE=:profile AND SUBJECT=:subject")
    fun allPeriodsLocalGrades(profile: Long, subject: Long): Flowable<List<LocalGrade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(grades: List<Grade>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrade(grade: LocalGrade): Long

    @Delete
    fun deleteGrade(grade: LocalGrade)

    @Query("DELETE FROM GRADE WHERE PROFILE = :profile")
    fun delete(profile: Long)
}
