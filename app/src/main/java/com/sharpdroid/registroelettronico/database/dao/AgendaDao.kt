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

    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE = :profile")
    fun getLocalAsSingle(profile: Long): Single<List<LocalAgendaPOJO>>

    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE=:profile AND ARCHIVED=0")
    fun getTodayAtSchool(profile: Long): LiveData<List<LocalAgenda>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: LocalAgenda)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBulk(event: List<LocalAgenda>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: List<RemoteAgenda>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: RemoteAgendaInfo)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInfos(event: List<RemoteAgendaInfo>)

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

    @Query("DELETE FROM REMOTE_AGENDA_INFO WHERE REMOTE_AGENDA_INFO.ID IN (SELECT REMOTE_AGENDA.ID FROM REMOTE_AGENDA WHERE REMOTE_AGENDA.PROFILE=:profile)")
    fun deleteRemoteInfo(profile: Long)


    @Query("DELETE FROM LOCAL_AGENDA WHERE PROFILE=:profile")
    fun deleteLocal(profile: Long)

    @Query("SELECT * FROM REMOTE_AGENDA_INFO WHERE ID=:id")
    fun getInfo(id: Long): RemoteAgendaInfo?

    @Query("SELECT REMOTE_AGENDA_INFO.* FROM REMOTE_AGENDA_INFO LEFT JOIN REMOTE_AGENDA ON REMOTE_AGENDA_INFO.ID=REMOTE_AGENDA.ID WHERE REMOTE_AGENDA.PROFILE=:profile")
    fun getRemoteInfos(profile: Long): Single<List<RemoteAgendaInfo>>

    @Query("SELECT * FROM REMOTE_AGENDA")
    fun getAllRemote(): Single<List<RemoteAgenda>>

    @Query("UPDATE LOCAL_AGENDA SET ARCHIVED=0")
    fun setLocalNotArchived()

    @Query("UPDATE REMOTE_AGENDA_INFO SET ARCHIVED=0")
    fun setRemoteNotArchived()
}
