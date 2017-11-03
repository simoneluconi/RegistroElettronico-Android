package com.sharpdroid.registroelettronico.database.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("SELECT * FROM FILE WHERE PROFILE=:profile AND TYPE!='file' AND ID NOT IN (SELECT ID FROM FILE_INFO)")
    List<File> getNoFiles(long profile);

    @Query("SELECT * FROM FOLDER WHERE TEACHER=:teacher AND PROFILE=:profile")
    List<Folder> getFolders(long teacher, long profile);

    @Query("DELETE FROM FILE WHERE PROFILE=:profile")
    void deleteFiles(long profile);

    @Query("DELETE FROM FOLDER WHERE PROFILE=:profile")
    void deleteFolders(long profile);

    @Insert
    void insert(List<Folder> folders);

    @Insert
    void insertFiles(List<File> files);

    @Insert
    void insert(FileInfo... fileInfo);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(FileInfo info);
}
