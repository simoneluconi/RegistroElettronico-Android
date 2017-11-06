package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "TEACHER")
class Teacher(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("teacherId") var id: Long = 0L,
        @ColumnInfo(name = "TEACHER_NAME") @Expose var teacherName: String = "",
        @Ignore @Expose var folders: List<Folder> = emptyList()
) : Serializable {
    constructor() : this(0, "", emptyList())
}

class DidacticAPI(@Expose @SerializedName("didacticts") val didactics: List<Teacher>)