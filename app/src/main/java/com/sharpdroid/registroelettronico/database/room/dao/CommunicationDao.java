package com.sharpdroid.registroelettronico.database.room.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface CommunicationDao {

    @Query("DELETE FROM COMMUNICATION WHERE PROFILE=:profile")
    void delete(long profile);
}
