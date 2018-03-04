package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Absence
import java.util.*

@Dao
abstract class AbsenceDao {

    @Query("SELECT * FROM ABSENCE WHERE DATE = :date AND PROFILE = :profile")
    abstract fun getAbsences(date: Date, profile: Long): LiveData<List<Absence>>

    @Query("SELECT * FROM ABSENCE WHERE PROFILE=:profile AND TYPE='ABA0'")
    abstract fun getAbsences(profile: Long): List<Absence>

    @Query("SELECT * FROM ABSENCE WHERE PROFILE=:profile AND TYPE!='ABA0'")
    abstract fun getNoAbsences(profile: Long): List<Absence>

    @Query("DELETE FROM ABSENCE WHERE PROFILE=:profile")
    abstract fun delete(profile: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(events: List<Absence>)

    @Transaction
    open fun removeAndInsert(profile: Long, events: List<Absence>) {
        delete(profile)
        insert(events)
    }
}
