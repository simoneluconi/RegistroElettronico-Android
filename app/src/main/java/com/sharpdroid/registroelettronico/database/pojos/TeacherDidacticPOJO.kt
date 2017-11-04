package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Relation
import com.sharpdroid.registroelettronico.database.entities.File
import com.sharpdroid.registroelettronico.database.entities.Folder
import com.sharpdroid.registroelettronico.database.entities.Teacher
import java.util.*

data class TeacherDidacticPOJO(
        @Embedded var teacher: Teacher,
        @Relation(entityColumn = "TEACHER", parentColumn = "ID", entity = Folder::class) var folders: List<FolderPOJO> = emptyList()
) {
    constructor() : this(Teacher(), emptyList())
}

data class FolderPOJO(
        @ColumnInfo(name = "ID") @PrimaryKey(autoGenerate = true) var id: Long = 0L,
        @ColumnInfo(name = "FOLDER_ID") var folderId: Int = -1,
        @ColumnInfo(name = "NAME") var name: String = "",
        @ColumnInfo(name = "LAST_UPDATE") var lastUpdate: Date,
        @Relation(parentColumn = "ID", entityColumn = "FOLDER") var files: List<File>,
        @ColumnInfo(name = "TEACHER") var teacher: Long = 0L,
        @ColumnInfo(name = "PROFILE") var profile: Long
) {
    constructor() : this(0, -1, "", Date(0), emptyList(), 0, 0)
}