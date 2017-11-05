package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.LocalGrade

@Dao
interface GradeDao {

    @Query("SELECT * FROM GRADE WHERE PROFILE = :profile")
    fun getGrades(profile: Long): LiveData<List<Grade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(grades: List<Grade>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrade(grade: LocalGrade): Long

    @Delete
    fun deleteGrade(grade: LocalGrade)

    @Query("DELETE FROM GRADE WHERE PROFILE = :profile")
    fun delete(profile: Long)
}
