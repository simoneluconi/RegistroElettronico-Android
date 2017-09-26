package com.sharpdroid.registroelettronico.Databases.DAOs

import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.Databases.Entities.Profile
import io.reactivex.Flowable

@Dao interface ProfileDAO {
    @Query("SELECT * FROM profiles WHERE username = :username LIMIT 1")
    fun getProfile(username: String): Flowable<Profile>

    @Query("SELECT * FROM profiles")
    fun getProfiles(username: String): Flowable<List<Profile>>

    @Update()
    fun updateProfiles(vararg profile: Profile)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addProfile(profile: Profile)
}