package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sharpdroid.registroelettronico.database.entities.Absence
import java.util.*

@Dao
interface AbsenceDao {

    @Query("SELECT * FROM ABSENCE WHERE DATE = :date AND PROFILE = :profile")
    fun getAbsences(date: Date, profile: Long): LiveData<List<Absence>>

    @Query("SELECT * FROM ABSENCE WHERE PROFILE=:profile AND TYPE='ABA0'")
    fun getAbsences(profile: Long): List<Absence>

    @Query("SELECT * FROM ABSENCE WHERE PROFILE=:profile AND TYPE!='ABA0'")
    fun getNoAbsences(profile: Long): List<Absence>

    @Query("DELETE FROM ABSENCE WHERE PROFILE=:profile")
    fun delete(profile: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(events: List<Absence>)
}
