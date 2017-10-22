package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Ignore
import com.orm.dsl.Table
import com.orm.dsl.Unique
import java.io.Serializable

@Table
data class Teacher(
        @Expose @Unique @SerializedName("teacherId") var id: Long,
        @Expose var teacherName: String,
        @Expose @Ignore var folders: List<Folder> //not present in /subjects
) : Serializable {
    constructor() : this(0, "", emptyList())
}

data class DidacticAPI(@Expose @SerializedName("didacticts") val didactics: List<Teacher>)