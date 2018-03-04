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
abstract class AgendaDao {

    @Transaction
    @Query("SELECT REMOTE_AGENDA.* FROM REMOTE_AGENDA LEFT JOIN REMOTE_AGENDA_INFO ON REMOTE_AGENDA.ID=REMOTE_AGENDA_INFO.ID WHERE PROFILE = :profile AND (ARCHIVED IS NULL OR ARCHIVED=0)")
    abstract fun getRemote(profile: Long): LiveData<List<RemoteAgendaPOJO>>

    @Transaction
    @Query("SELECT REMOTE_AGENDA.* FROM REMOTE_AGENDA LEFT JOIN REMOTE_AGENDA_INFO ON REMOTE_AGENDA.ID=REMOTE_AGENDA_INFO.ID WHERE PROFILE = :profile AND (ARCHIVED IS NULL OR ARCHIVED=0)")
    abstract fun getRemoteSync(profile: Long): List<RemoteAgendaPOJO>

    @Query("SELECT * FROM REMOTE_AGENDA WHERE PROFILE = :profile")
    abstract fun getRemoteList(profile: Long): List<RemoteAgenda>

    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE = :profile AND ARCHIVED!=1")
    abstract fun getLocal(profile: Long): LiveData<List<LocalAgendaPOJO>>

    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE = :profile AND ARCHIVED!=1")
    abstract fun getLocalSync(profile: Long): List<LocalAgendaPOJO>

    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE = :profile")
    abstract fun getLocalAsSingle(profile: Long): Single<List<LocalAgenda>>

    @Query("SELECT * FROM LOCAL_AGENDA WHERE PROFILE=:profile AND ARCHIVED=0")
    abstract fun getTodayAtSchool(profile: Long): LiveData<List<LocalAgenda>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLocal(event: LocalAgenda)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLocal(event: List<LocalAgenda>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRemote(event: List<RemoteAgenda>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRemoteInfo(event: RemoteAgendaInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRemoteInfo(event: List<RemoteAgendaInfo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(event: LocalAgenda): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(event: RemoteAgenda): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(event: RemoteAgendaInfo): Int

    @Query("DELETE FROM REMOTE_AGENDA WHERE PROFILE = :profile")
    abstract fun deleteRemote(profile: Long)

    @Transaction
    open fun removeRemoteAndInsert(profile: Long, event: List<RemoteAgenda>) {
        deleteRemote(profile)
        insertRemote(event)
    }

    @Query("DELETE FROM REMOTE_AGENDA_INFO")
    abstract fun deleteRemoteInfo()

    @Query("DELETE FROM REMOTE_AGENDA_INFO WHERE REMOTE_AGENDA_INFO.ID IN (SELECT REMOTE_AGENDA.ID FROM REMOTE_AGENDA WHERE REMOTE_AGENDA.PROFILE=:profile)")
    abstract fun deleteRemoteInfo(profile: Long)

    @Query("DELETE FROM LOCAL_AGENDA WHERE PROFILE=:profile")
    abstract fun deleteLocal(profile: Long)

    @Query("SELECT * FROM REMOTE_AGENDA_INFO WHERE ID=:id")
    abstract fun getRemoteInfo(id: Long): RemoteAgendaInfo?

    @Query("SELECT REMOTE_AGENDA_INFO.* FROM REMOTE_AGENDA_INFO LEFT JOIN REMOTE_AGENDA ON REMOTE_AGENDA_INFO.ID=REMOTE_AGENDA.ID WHERE REMOTE_AGENDA.PROFILE=:profile")
    abstract fun getRemoteInfoAsSingle(profile: Long): Single<List<RemoteAgendaInfo>>

    @Query("SELECT * FROM REMOTE_AGENDA")
    abstract fun getAllRemoteAsSingle(): Single<List<RemoteAgenda>>

    @Query("UPDATE LOCAL_AGENDA SET ARCHIVED=0")
    abstract fun setLocalNotArchived()

    @Query("UPDATE REMOTE_AGENDA_INFO SET ARCHIVED=0")
    abstract fun setRemoteNotArchived()

    @Query("SELECT * FROM REMOTE_AGENDA WHERE ID=:id LIMIT 1")
    abstract fun byIdRemote(id: Long): RemoteAgenda?

}
