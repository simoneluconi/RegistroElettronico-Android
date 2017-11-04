package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.sharpdroid.registroelettronico.database.entities.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM NOTE WHERE PROFILE=:profile ORDER BY M_DATE DESC")
    LiveData<List<Note>> getNotes(long profile);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Note> notes);

    @Query("DELETE FROM NOTE WHERE PROFILE = :profile")
    void delete(long profile);
}
