package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.Communication
import com.sharpdroid.registroelettronico.database.entities.CommunicationInfo
import io.reactivex.Flowable

@Dao
abstract class CommunicationDao {

    @Query("SELECT * FROM COMMUNICATION_INFO WHERE ID = :id")
    abstract fun getInfo(id: Long): CommunicationInfo?

    @Query("SELECT * FROM COMMUNICATION_INFO")
    abstract fun getAllInfo(): List<CommunicationInfo>

    @Query("SELECT * FROM COMMUNICATION WHERE PROFILE = :profile ORDER BY DATE DESC")
    abstract fun loadCommunications(profile: Long): LiveData<List<Communication>>

    @Query("SELECT * FROM COMMUNICATION WHERE PROFILE = :profile ORDER BY DATE DESC")
    abstract fun loadCommunicationsFlow(profile: Long): Flowable<List<Communication>>

    @Query("SELECT * FROM COMMUNICATION WHERE PROFILE = :profile ORDER BY DATE DESC")
    abstract fun getCommunicationsList(profile: Long): List<Communication>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(communications: List<Communication>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(communications: CommunicationInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(communications: Communication)

    @Query("DELETE FROM COMMUNICATION WHERE PROFILE=:profile")
    abstract fun delete(profile: Long)

    @Transaction
    open fun removeAndInsert(profile: Long, communications: List<Communication>) {
        delete(profile)
        insert(communications)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(info: CommunicationInfo): Int

}
