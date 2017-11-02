package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface FolderDao {
    @Query("DELETE FROM FILE WHERE PROFILE=:profile")
    void deleteFiles(long profile);

    @Query("DELETE FROM FOLDER WHERE PROFILE=:profile")
    void deleteFolders(long profile);
}
