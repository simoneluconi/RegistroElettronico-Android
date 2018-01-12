package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.TimetableItem

@Dao
abstract class TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(timetableItem: TimetableItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(timetableItem: List<TimetableItem>)

    @Query("SELECT * FROM TimetableItem WHERE profile=:profile")
    abstract fun queryProfile(profile: Long): LiveData<List<TimetableItem>>


    @Query("SELECT * FROM TimetableItem WHERE id=:id LIMIT 1")
    abstract fun findById(id: Long): TimetableItem?

    @Query("SELECT color FROM TimetableItem WHERE subject=:subject GROUP BY color ORDER BY id DESC LIMIT 1")
    abstract fun colors(subject: Long): Int?

    @Delete
    abstract fun delete(timetableItem: TimetableItem)

    @Query("DELETE FROM TimetableItem WHERE PROFILE=:profile")
    abstract fun deleteProfile(profile: Long)

    @Transaction
    open fun removeAndInsert(profile: Long, timetableItem: List<TimetableItem>) {
        deleteProfile(profile)
        insert(timetableItem)
    }
}