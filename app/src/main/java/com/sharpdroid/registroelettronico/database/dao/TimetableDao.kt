package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.TimetableItem

@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timetableItem: TimetableItem)

    @Query("SELECT * FROM TimetableItem WHERE profile=:profile")
    fun queryProfile(profile: Long): LiveData<List<TimetableItem>>

    @Delete
    fun delete(timetableItem: TimetableItem)
}