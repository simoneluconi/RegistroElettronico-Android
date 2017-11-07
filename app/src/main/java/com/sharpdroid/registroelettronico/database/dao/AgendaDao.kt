package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda
import com.sharpdroid.registroelettronico.database.entities.RemoteAgendaInfo
import com.sharpdroid.registroelettronico.database.pojos.LocalAgendaPOJO
import com.sharpdroid.registroelettronico.database.pojos.RemoteAgendaPOJO
import io.reactivex.Single

@Dao
interface AgendaDao {

    @Query("SELECT REMOTE_AGENDA.* FROM REMOTE_AGENDA LEFT JOIN REMOTE_AGENDA_INFO ON REMOTE_AGENDA.ID=REMOTE_AGENDA_INFO.ID WHERE PROFILE = :profile AND (ARCHIVED IS NULL OR ARCHIVED=0)")
    fun getRemote(profile: Long): LiveData<List<RemoteAgendaPOJO>>


    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE = :profile AND ARCHIVED!=1")
    fun getLocal(profile: Long): LiveData<List<LocalAgendaPOJO>>

    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE = :profile AND ARCHIVED!=1")
    fun getLocalAsSingle(profile: Long): Single<List<LocalAgendaPOJO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: LocalAgenda)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBulk(event: List<LocalAgenda>)

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


    @Query("DELETE FROM LOCAL_AGENDA WHERE PROFILE=:profile")
    fun deleteLocal(profile: Long)

    @Query("UPDATE LOCAL_AGENDA SET ARCHIVED=0 WHERE ARCHIVED!=0")
    fun setNotArchived()

    @Query("SELECT * FROM REMOTE_AGENDA_INFO WHERE ID=:id")
    fun getInfo(id: Long): RemoteAgendaInfo?
}
