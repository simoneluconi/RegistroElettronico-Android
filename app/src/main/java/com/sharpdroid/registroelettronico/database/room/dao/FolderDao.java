package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.sharpdroid.registroelettronico.database.entities.File;
import com.sharpdroid.registroelettronico.database.entities.FileInfo;
import com.sharpdroid.registroelettronico.database.entities.Folder;

import java.util.List;

@Dao
public interface FolderDao {

    @Query("SELECT * FROM FILE_INFO WHERE ID = :id LIMIT 1")
    FileInfo getInfo(long id);

    @Query("SELECT * FROM FILE WHERE TEACHER=:teacherId AND FOLDER=:folderId")
    LiveData<List<File>> getFiles(long teacherId, long folderId);

    @Query("SELECT * FROM FOLDER WHERE TEACHER=:teacher AND PROFILE=:profile")
    List<Folder> getFolders(long teacher, long profile);

    @Query("DELETE FROM FILE WHERE PROFILE=:profile")
    void deleteFiles(long profile);

    @Query("DELETE FROM FOLDER WHERE PROFILE=:profile")
    void deleteFolders(long profile);
}
