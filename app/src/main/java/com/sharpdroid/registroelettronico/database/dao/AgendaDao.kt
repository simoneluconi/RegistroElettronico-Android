package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda
import com.sharpdroid.registroelettronico.database.entities.RemoteAgendaInfo

@Dao
interface AgendaDao {

    @Query("SELECT * FROM REMOTE_AGENDA WHERE PROFILE = :profile")
    fun getRemote(profile: Long): LiveData<List<RemoteAgenda>>


    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE = :profile")
    fun getLocal(profile: Long): LiveData<List<LocalAgenda>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: LocalAgenda)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: List<RemoteAgenda>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: RemoteAgendaInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(event: LocalAgenda): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(event: RemoteAgenda): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(event: RemoteAgendaInfo): Int

    @Query("DELETE FROM REMOTE_AGENDA WHERE PROFILE = :profile")
    fun deleteRemote(profile: Long)

    @Query("DELETE FROM REMOTE_AGENDA_INFO")
    fun deleteRemoteInfo()

    @Query("UPDATE LOCAL_AGENDA SET ARCHIVED=0 WHERE ARCHIVED!=0")
    fun setNotArchived()
}
