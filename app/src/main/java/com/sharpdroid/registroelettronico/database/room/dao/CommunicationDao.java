package com.sharpdroid.registroelettronico.database.room.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sharpdroid.registroelettronico.database.entities.Communication;
import com.sharpdroid.registroelettronico.database.entities.CommunicationInfo;

import java.util.List;

@Dao
public interface CommunicationDao {

    @Query("SELECT * FROM COMMUNICATION_INFO WHERE ID = :id")
    CommunicationInfo getInfo(long id);

    @Query("SELECT * FROM COMMUNICATION WHERE PROFILE = :profile ORDER BY DATE DESC")
    LiveData<List<Communication>> loadCommunications(long profile);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Communication> communications);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CommunicationInfo communications);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Communication communications);

    @Query("DELETE FROM COMMUNICATION WHERE PROFILE=:profile")
    void delete(long profile);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(CommunicationInfo info);
}
