package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.TimetableItem

@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timetableItem: TimetableItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timetableItem: List<TimetableItem>)

    @Query("SELECT * FROM TimetableItem WHERE profile=:profile")
    fun queryProfile(profile: Long): LiveData<List<TimetableItem>>


    @Query("SELECT * FROM TimetableItem WHERE id=:id LIMIT 1")
    fun findById(id: Long): TimetableItem?

    @Query("SELECT color FROM TimetableItem WHERE subject=:subject GROUP BY color ORDER BY id DESC LIMIT 1")
    fun colors(subject: Long): Int?

    @Delete
    fun delete(timetableItem: TimetableItem)

    @Query("DELETE FROM TimetableItem WHERE PROFILE=:profile")
    fun deleteProfile(profile: Long)
}