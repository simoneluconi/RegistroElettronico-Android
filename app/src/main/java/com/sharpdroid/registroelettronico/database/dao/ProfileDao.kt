package com.sharpdroid.registroelettronico.database.dao

import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Profile

@Dao
interface ProfileDao {

    @get:Query("SELECT * FROM PROFILE")
    val profilesSync: List<Profile>

    @get:Query("SELECT * FROM PROFILE LIMIT 1")
    val randomProfile: Profile?

    @Query("SELECT * FROM PROFILE WHERE ID = :profile LIMIT 1")
    fun getProfile(profile: Long): Profile

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: Profile)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(profile: Profile)

    @Query("DELETE FROM PROFILE WHERE ID = :profile")
    fun delete(profile: Long)
}
