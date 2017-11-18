package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Communication
import com.sharpdroid.registroelettronico.database.entities.CommunicationInfo

@Dao
interface CommunicationDao {

    @Query("SELECT * FROM COMMUNICATION_INFO WHERE ID = :id")
    fun getInfo(id: Long): CommunicationInfo?

    @Query("SELECT * FROM COMMUNICATION WHERE PROFILE = :profile ORDER BY DATE DESC")
    fun loadCommunications(profile: Long): LiveData<List<Communication>>

    @Query("SELECT * FROM COMMUNICATION WHERE PROFILE = :profile ORDER BY DATE DESC")
    fun getCommunicationsList(profile: Long): List<Communication>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(communications: List<Communication>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(communications: CommunicationInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(communications: Communication)

    @Query("DELETE FROM COMMUNICATION WHERE PROFILE=:profile")
    fun delete(profile: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(info: CommunicationInfo): Int
}
