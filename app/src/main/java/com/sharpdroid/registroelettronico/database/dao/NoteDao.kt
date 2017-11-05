package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import com.sharpdroid.registroelettronico.database.entities.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM NOTE WHERE PROFILE=:profile ORDER BY M_DATE DESC")
    fun getNotes(profile: Long): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notes: List<Note>)

    @Query("DELETE FROM NOTE WHERE PROFILE = :profile")
    fun delete(profile: Long)
}
