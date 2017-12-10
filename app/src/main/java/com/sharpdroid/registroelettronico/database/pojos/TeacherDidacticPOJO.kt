package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.sharpdroid.registroelettronico.database.entities.File
import com.sharpdroid.registroelettronico.database.entities.Folder
import com.sharpdroid.registroelettronico.database.entities.Teacher
import java.util.*

data class TeacherDidacticPOJO(
        @Embedded var teacher: Teacher = Teacher(),
        @Relation(entityColumn = "TEACHER", parentColumn = "ID", entity = Folder::class) var folders: List<FolderPOJO> = emptyList()
)

data class FolderPOJO(
        @ColumnInfo(name = "ID") var id: Long = 0L,
        @ColumnInfo(name = "FOLDER_ID") var folderId: Int = -1,
        @ColumnInfo(name = "NAME") var name: String = "",
        @ColumnInfo(name = "LAST_UPDATE") var lastUpdate: Date = Date(0),
        @Relation(parentColumn = "ID", entityColumn = "FOLDER") var files: List<File> = emptyList(),
        @ColumnInfo(name = "TEACHER") var teacher: Long = 0L,
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L
)