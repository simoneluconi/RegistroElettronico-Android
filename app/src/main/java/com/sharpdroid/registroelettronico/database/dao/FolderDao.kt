package com.sharpdroid.registroelettronico.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sharpdroid.registroelettronico.database.entities.File
import com.sharpdroid.registroelettronico.database.entities.FileInfo
import com.sharpdroid.registroelettronico.database.entities.Folder
import com.sharpdroid.registroelettronico.database.pojos.TeacherDidacticPOJO
import io.reactivex.Single

@Dao
abstract class FolderDao {

    @Query("SELECT * FROM FILE_INFO WHERE ID = :id LIMIT 1")
    abstract fun getInfo(id: Long): FileInfo?

    @Query("SELECT * FROM FILE WHERE TEACHER=:teacherId AND FOLDER=:folderId")
    abstract fun getFiles(teacherId: Long, folderId: Long): LiveData<List<File>>

    @Query("SELECT * FROM FILE WHERE PROFILE=:profile AND TYPE!='file' AND ID NOT IN (SELECT ID FROM FILE_INFO)")
    abstract fun getNoFiles(profile: Long): List<File>

    @Query("SELECT * FROM FOLDER WHERE TEACHER=:teacher AND PROFILE=:profile")
    abstract fun getFolders(teacher: Long, profile: Long): List<Folder>

    @Query("DELETE FROM FILE WHERE PROFILE=:profile")
    abstract fun deleteFiles(profile: Long)

    @Query("DELETE FROM FOLDER WHERE PROFILE=:profile")
    abstract fun deleteFolders(profile: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(folders: List<Folder>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(folders: Folder): Long

    open fun getDidattica(profile: Long): Single<List<TeacherDidacticPOJO>> {
        return getDidattica_(profile).map { data: List<TeacherDidacticPOJO> ->
            val mutable = data.toMutableList()

            mutable.forEach { it.folders = it.folders.filter { it.profile == profile } }

            return@map mutable
        }
    }

    @Transaction
    @Query("SELECT * FROM TEACHER WHERE ID IN (SELECT FOLDER.TEACHER FROM FOLDER WHERE PROFILE=:profile GROUP BY FOLDER.TEACHER)")
    abstract fun getDidattica_(profile: Long): Single<List<TeacherDidacticPOJO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFiles(files: List<File>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg fileInfo: FileInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(info: FileInfo): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(info: FileInfo): Long
}
