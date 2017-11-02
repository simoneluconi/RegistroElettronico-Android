package com.sharpdroid.registroelettronico.database.room.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.sharpdroid.registroelettronico.database.entities.Absence;

import java.util.Date;
import java.util.List;

@Dao
public interface AbsenceDao {
    @Query("SELECT * FROM ABSENCE WHERE DATE = :date AND PROFILE = :profile")
    LiveData<List<Absence>> getAbsences(Date date, Long profile);

    @Query("SELECT * FROM ABSENCE WHERE PROFILE=:profile AND TYPE='ABA0' ORDER BY DATE DESC")
    List<Absence> getAbsences(long profile);

    @Query("DELETE FROM ABSENCE WHERE PROFILE=:profile")
    void delete(long profile);
}
